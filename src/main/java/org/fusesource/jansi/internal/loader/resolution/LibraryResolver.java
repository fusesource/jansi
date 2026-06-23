package org.fusesource.jansi.internal.loader.resolution;

import org.fusesource.jansi.internal.OSInfo;

public class LibraryResolver {

    private static final String LIBRARY_NAME_PROPERTY = "library.jansi.name";
    private static final String DYLIB_EXT = ".dylib";
    private static final String JNILIB_EXT = ".jnilib";

    private LibraryResolver() {}

    public static String resolveLibraryName() {
        String customName = System.getProperty(LIBRARY_NAME_PROPERTY);
        if (customName != null) {
            return customName;
        }
        String name = System.mapLibraryName("jansi");
        if (name.endsWith(DYLIB_EXT)) {
            name = name.replace(DYLIB_EXT, JNILIB_EXT);
        }
        return name;
    }

    public static String resolvePackagePath() {
        // Adjusts package lookup relative to the new structure
        String basePackage = "org/fusesource/jansi/internal";
        return String.format("/%s/native/%s",
                basePackage, OSInfo.getNativeLibFolderPathForCurrentOS());
    }
}