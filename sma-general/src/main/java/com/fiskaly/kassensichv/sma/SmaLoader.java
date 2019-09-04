package com.fiskaly.kassensichv.sma;

import jnr.ffi.LibraryLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        extensions.put("mac os x", ".dylib");

        architectures = new HashMap<>();

        architectures.put("x86_64", "amd64");
        architectures.put("amd64", "amd64");
        architectures.put("x32", "386");
    }

    private static void unpackLibraryIntoTempDirectory() throws IOException {
        String libName = buildLibraryName();
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        ClassLoader classLoader = SmaLoader.class.getClassLoader();

        InputStream libraryStream = classLoader.getResourceAsStream(libName);
        File target = new File(tmpDirectory + File.separator + libName);
        OutputStream targetOutStream = null;

        try {
            targetOutStream = new FileOutputStream(target);

            byte[] buffer = new byte[1000000];
            int currentBufferSize = libraryStream.read(buffer);

            while (currentBufferSize != -1) {
                targetOutStream.write(buffer, 0, currentBufferSize);
                currentBufferSize = libraryStream.read(buffer);
            }

        } finally {
            libraryStream.close();

            if (targetOutStream != null) {
                targetOutStream.close();
            }
        }
    }

    private static String buildLibraryName() {
        String osArchitecture = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String libArchitecture = architectures.containsKey(osArchitecture) ?
                architectures.get(osArchitecture) : architectures.get("386");
        String libExtension = extensions.get(osName);

        return "com.fiskaly.kassensichv.sma-" + osName + "-" + libArchitecture + libExtension;
    }

    private static String getLibraryPath() {
        String libraryName = buildLibraryName();

        return System.getProperty("java.io.tmpdir") + File.separator + libraryName;
    }

    public static SmaLibrary load() throws IOException {
        unpackLibraryIntoTempDirectory();

        String libraryPath = getLibraryPath();

        LibraryLoader<SmaLibrary> loader = LibraryLoader.create(SmaLibrary.class);
        return loader.load(libraryPath);
    }
}
