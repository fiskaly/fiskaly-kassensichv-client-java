package com.fiskaly.kassensichv.client.sma;

import jnr.ffi.LibraryLoader;

import java.util.HashMap;
import java.util.Map;

public class SmaLoader {
    private static Map<String, String> extensions;
    private static Map<String, String> architectures;

    static {
        extensions = new HashMap<>();

        extensions.put("linux", ".so");
        extensions.put("windows", ".dll");
        extensions.put("osx", ".dylib");

        architectures = new HashMap<>();

        architectures.put("x86_64", "x64");
        architectures.put("amd64", "x64");
        architectures.put("x32", "x32");
    }

    private static String buildLibraryName() {
        String osArchitecture = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String libArchitecture = architectures.containsKey(osArchitecture) ?
                architectures.get(osArchitecture) : architectures.get("x32");
        String libExtension = extensions.get(osName);

        return "sma-" + osName + "-" + libArchitecture + libExtension;
    }

    private static String getLibraryPath() {
        String libraryName = buildLibraryName();

        ClassLoader classLoader = SmaLoader.class.getClassLoader();
        String path = classLoader
                .getResource(libraryName)
                .getFile();

        return path;
    }

    public static SmaLibrary load() {
        LibraryLoader<SmaLibrary> loader = LibraryLoader.create(SmaLibrary.class);
        return loader.load(getLibraryPath());
    }
}
