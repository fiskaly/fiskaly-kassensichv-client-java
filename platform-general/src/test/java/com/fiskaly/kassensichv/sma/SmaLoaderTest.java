package com.fiskaly.kassensichv.sma;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SmaLoaderTest {
    @Test
    public void buildCorrectLibraryPath() {
        Properties properties = new Properties();
        properties.setProperty("os.arch", "x32");
        properties.setProperty("os.name", "Windows 10");

        System.setProperties(properties);

        String libraryName = SmaLoader.buildLibraryName();

        assertTrue(libraryName.contains("com.fiskaly.kassensichv.sma"));
        assertEquals("com.fiskaly.kassensichv.sma-"
                + SmaLoader.OS_WINDOWS
                + "-"
                + SmaLoader.SMA_LIB_32
                + ".dll", libraryName);
    }

    @Test
    public void shouldFallbackToDefaults() {
        Properties properties = new Properties();
        properties.setProperty("os.arch", "blub");
        properties.setProperty("os.name", "ToasterOS");

        System.setProperties(properties);

        String libraryName = SmaLoader.buildLibraryName();

        assertTrue(libraryName.contains("com.fiskaly.kassensichv.sma"));

        String should = "com.fiskaly.kassensichv.sma-"
                + SmaLoader.OS_LINUX + "-" + SmaLoader.SMA_LIB_32
                + ".so";

        assertEquals(should, libraryName);
    }
}
