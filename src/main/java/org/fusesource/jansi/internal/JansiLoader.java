package org.fusesource.jansi.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.internal.loader.config.JansiEnvironment;
import org.fusesource.jansi.internal.loader.io.ContentVerifier;
import org.fusesource.jansi.internal.loader.io.LibraryExtractor;
import org.fusesource.jansi.internal.loader.io.TempFileCleanup;
import org.fusesource.jansi.internal.loader.resolution.LibraryResolver;

public class JansiLoader {

    private static final Logger LOG = Logger.getLogger(JansiLoader.class.getName());
    private static final String LIBRARY_PATH_PROPERTY = "library.jansi.path";

    private static boolean loaded = false;
    private static String nativeLibraryPath;
    private static String nativeLibrarySourceUrl;

    private JansiLoader() {}

    public static synchronized boolean initialize() {
        if (!loaded) {
            TempFileCleanup.cleanup(JansiEnvironment.getTempDir(), JansiEnvironment.getVersion());
        }
        try {
            loadJansiNativeLibrary();
        } catch (Exception e) {
            boolean graceful = Boolean.parseBoolean(
                    System.getProperty(AnsiConsole.JANSI_GRACEFUL, "true"));
            if (!graceful) {
                throw new RuntimeException("Unable to load jansi native library.", e);
            }
            LOG.warning("Failed to load jansi native library: " + e.getMessage());
        }
        return loaded;
    }

    public static String getNativeLibraryPath() {
        return nativeLibraryPath;
    }

    public static String getNativeLibrarySourceUrl() {
        return nativeLibrarySourceUrl;
    }

    private static void loadJansiNativeLibrary() throws Exception {
        if (loaded) return;

        List<String> triedPaths = new ArrayList<>();
        String libraryName = LibraryResolver.resolveLibraryName();

        // Path 1: User-defined system property
        String userDefinedPath = System.getProperty(LIBRARY_PATH_PROPERTY);
        if (userDefinedPath != null) {
            String withOsSubfolder = userDefinedPath + "/" + OSInfo.getNativeLibFolderPathForCurrentOS();
            if (performSystemLoad(new File(withOsSubfolder, libraryName))) return;
            triedPaths.add(withOsSubfolder);

            if (performSystemLoad(new File(userDefinedPath, libraryName))) return;
            triedPaths.add(userDefinedPath);
        }

        // Path 2: Bundled inside the JAR
        String jarLibraryPath = LibraryResolver.resolvePackagePath();
        if (JansiLoader.class.getResource(jarLibraryPath + "/" + libraryName) != null) {
            String tempFolder = JansiEnvironment.getTempDir().getAbsolutePath();
            if (extractAndLoad(jarLibraryPath, libraryName, tempFolder)) return;
            triedPaths.add(jarLibraryPath);
        }

        // Path 3: Standard java.library.path
        String javaLibraryPath = System.getProperty("java.library.path", "");
        for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (ldPath.isEmpty()) continue;
            if (performSystemLoad(new File(ldPath, libraryName))) return;
            triedPaths.add(ldPath);
        }

        throw new Exception(String.format(
                "No native library found for os.name=%s, os.arch=%s, paths=[%s]",
                OSInfo.getOSName(), OSInfo.getArchName(), String.join(File.pathSeparator, triedPaths)));
    }

    private static boolean extractAndLoad(String libFolder, String libraryFileName, String targetFolder) {
        String resourcePath = libFolder + "/" + libraryFileName;
        String uniqueName = String.format("jansi-%s-%s-%s",
                JansiEnvironment.getVersion(), UUID.randomUUID(), libraryFileName);

        File extractedLibFile = new File(targetFolder, uniqueName);
        File extractedLckFile = new File(targetFolder, uniqueName + ".lck");

        try (InputStream extractStream = JansiLoader.class.getResourceAsStream(resourcePath);
             InputStream verifyStream = JansiLoader.class.getResourceAsStream(resourcePath)) {

            LibraryExtractor.extractLibraryToFile(extractStream, extractedLibFile, extractedLckFile);
            LibraryExtractor.setExecutablePermissions(extractedLibFile);
            ContentVerifier.verifyExtractedContents(verifyStream, extractedLibFile);

            if (performSystemLoad(extractedLibFile)) {
                nativeLibrarySourceUrl = JansiLoader.class.getResource(resourcePath).toExternalForm();
                return true;
            }
        } catch (IOException e) {
            LOG.warning("Failed to extract or verify native library: " + e.getMessage());
        }
        return false;
    }

    private static boolean performSystemLoad(File libFile) {
        if (!libFile.exists()) return false;
        try {
            System.load(libFile.getAbsolutePath());
            nativeLibraryPath = libFile.getAbsolutePath();
            loaded = true;
            return true;
        } catch (UnsatisfiedLinkError e) {
            LOG.warning("Failed to load native library '" + libFile.getName() + "': " + e.getMessage());
            return false;
        }
    }
}