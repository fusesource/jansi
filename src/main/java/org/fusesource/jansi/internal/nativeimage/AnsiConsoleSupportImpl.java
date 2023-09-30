package org.fusesource.jansi.internal.nativeimage;

import org.fusesource.jansi.AnsiConsoleSupport;

public class AnsiConsoleSupportImpl implements AnsiConsoleSupport {
    @Override
    public String getProviderName() {
        return "native-image";
    }

    @Override
    public CLibrary getCLibrary() {
        return null; // TODO
    }

    @Override
    public Kernel32 getKernel32() {
        return null; // TODO
    }
}
