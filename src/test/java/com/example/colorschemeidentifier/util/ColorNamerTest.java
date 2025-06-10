package com.example.colorschemeidentifier.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColorNamerTest {

    @Test
    void testGetNameFromHex_knownColors() {
        assertEquals("Black", ColorNamer.getNameFromHex("#000000"));
        assertEquals("White", ColorNamer.getNameFromHex("#FFFFFF"));
        assertEquals("Red", ColorNamer.getNameFromHex("#FF0000"));
        assertEquals("Lime", ColorNamer.getNameFromHex("#00FF00")); // Lime, not Green
        assertEquals("Blue", ColorNamer.getNameFromHex("#0000FF"));
        assertEquals("RebeccaPurple", ColorNamer.getNameFromHex("#663399"));
        // Test case insensitivity for hex
        assertEquals("Yellow", ColorNamer.getNameFromHex("#ffff00"));
    }

    @Test
    void testGetNameFromHex_unknownColor() {
        assertNull(ColorNamer.getNameFromHex("#123456"));
    }

    @Test
    void testGetNameFromHex_invalidInputs() {
        assertNull(ColorNamer.getNameFromHex(null));
        assertNull(ColorNamer.getNameFromHex(""));
        assertNull(ColorNamer.getNameFromHex("#123")); // Invalid length after normalization
        assertNull(ColorNamer.getNameFromHex("invalid"));
    }

    @Test
    void testGetHexFromName_knownColors() {
        assertEquals("#000000", ColorNamer.getHexFromName("Black"));
        assertEquals("#FFFFFF", ColorNamer.getHexFromName("White"));
        assertEquals("#FF0000", ColorNamer.getHexFromName("Red"));
        assertEquals("#00FF00", ColorNamer.getHexFromName("Lime"));
        assertEquals("#0000FF", ColorNamer.getHexFromName("Blue"));
        assertEquals("#663399", ColorNamer.getHexFromName("RebeccaPurple"));
        // Test case insensitivity for name
        assertEquals("#FFFF00", ColorNamer.getHexFromName("yellow"));
        assertEquals("#FF00FF", ColorNamer.getHexFromName("magenta")); // Magenta preferred over Fuchsia for #FF00FF
        assertEquals("#FF00FF", ColorNamer.getHexFromName("fuchsia"));
    }

    @Test
    void testGetHexFromName_unknownColor() {
        assertNull(ColorNamer.getHexFromName("SuperDuperBlue"));
    }

    @Test
    void testGetHexFromName_invalidInputs() {
        assertNull(ColorNamer.getHexFromName(null));
        assertNull(ColorNamer.getHexFromName(""));
    }

    @Test
    void testIsKnownColorName() {
        assertTrue(ColorNamer.isKnownColorName("Red"));
        assertTrue(ColorNamer.isKnownColorName("red"));
        assertFalse(ColorNamer.isKnownColorName("UnknownColor"));
        assertFalse(ColorNamer.isKnownColorName(null));
        assertFalse(ColorNamer.isKnownColorName(""));
    }

    @Test
    void testSpecificGrayGreyAliases() {
        // Check that preferred name is returned for hex, but alias name also resolves to hex
        assertEquals("DarkGray", ColorNamer.getNameFromHex("#A9A9A9"));
        assertEquals("#A9A9A9", ColorNamer.getHexFromName("DarkGray"));
        assertEquals("#A9A9A9", ColorNamer.getHexFromName("DarkGrey"));

        assertEquals("SlateGray", ColorNamer.getNameFromHex("#708090"));
        assertEquals("#708090", ColorNamer.getHexFromName("SlateGray"));
        assertEquals("#708090", ColorNamer.getHexFromName("SlateGrey"));
    }
}
