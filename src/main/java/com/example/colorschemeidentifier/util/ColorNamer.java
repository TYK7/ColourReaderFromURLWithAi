package com.example.colorschemeidentifier.util;

import java.util.HashMap;
import java.util.Map;

public class ColorNamer {

    private static final Map<String, String> HEX_TO_NAME = new HashMap<>();
    private static final Map<String, String> NAME_TO_HEX = new HashMap<>();

    static {
        // Populate the map with W3Schools 140 standard color names
        addBidirectionalMap("AliceBlue", "#F0F8FF");
        addBidirectionalMap("AntiqueWhite", "#FAEBD7");
        addBidirectionalMap("Aqua", "#00FFFF");
        addBidirectionalMap("Aquamarine", "#7FFFD4");
        addBidirectionalMap("Azure", "#F0FFFF");
        addBidirectionalMap("Beige", "#F5F5DC");
        addBidirectionalMap("Bisque", "#FFE4C4");
        addBidirectionalMap("Black", "#000000");
        addBidirectionalMap("BlanchedAlmond", "#FFEBCD");
        addBidirectionalMap("Blue", "#0000FF");
        addBidirectionalMap("BlueViolet", "#8A2BE2");
        addBidirectionalMap("Brown", "#A52A2A");
        addBidirectionalMap("BurlyWood", "#DEB887");
        addBidirectionalMap("CadetBlue", "#5F9EA0");
        addBidirectionalMap("Chartreuse", "#7FFF00");
        addBidirectionalMap("Chocolate", "#D2691E");
        addBidirectionalMap("Coral", "#FF7F50");
        addBidirectionalMap("CornflowerBlue", "#6495ED");
        addBidirectionalMap("Cornsilk", "#FFF8DC");
        addBidirectionalMap("Crimson", "#DC143C");
        addBidirectionalMap("Cyan", "#00FFFF");
        addBidirectionalMap("DarkBlue", "#00008B");
        addBidirectionalMap("DarkCyan", "#008B8B");
        addBidirectionalMap("DarkGoldenRod", "#B8860B");
        addBidirectionalMap("DarkGray", "#A9A9A9"); // Also DarkGrey
        addBidirectionalMap("DarkGreen", "#006400");
        addBidirectionalMap("DarkKhaki", "#BDB76B");
        addBidirectionalMap("DarkMagenta", "#8B008B");
        addBidirectionalMap("DarkOliveGreen", "#556B2F");
        addBidirectionalMap("DarkOrange", "#FF8C00");
        addBidirectionalMap("DarkOrchid", "#9932CC");
        addBidirectionalMap("DarkRed", "#8B0000");
        addBidirectionalMap("DarkSalmon", "#E9967A");
        addBidirectionalMap("DarkSeaGreen", "#8FBC8F");
        addBidirectionalMap("DarkSlateBlue", "#483D8B");
        addBidirectionalMap("DarkSlateGray", "#2F4F4F"); // Also DarkSlateGrey
        addBidirectionalMap("DarkTurquoise", "#00CED1");
        addBidirectionalMap("DarkViolet", "#9400D3");
        addBidirectionalMap("DeepPink", "#FF1493");
        addBidirectionalMap("DeepSkyBlue", "#00BFFF");
        addBidirectionalMap("DimGray", "#696969"); // Also DimGrey
        addBidirectionalMap("DodgerBlue", "#1E90FF");
        addBidirectionalMap("FireBrick", "#B22222");
        addBidirectionalMap("FloralWhite", "#FFFAF0");
        addBidirectionalMap("ForestGreen", "#228B22");
        addBidirectionalMap("Fuchsia", "#FF00FF"); // Same as Magenta
        addBidirectionalMap("Gainsboro", "#DCDCDC");
        addBidirectionalMap("GhostWhite", "#F8F8FF");
        addBidirectionalMap("Gold", "#FFD700");
        addBidirectionalMap("GoldenRod", "#DAA520");
        addBidirectionalMap("Gray", "#808080"); // Also Grey
        addBidirectionalMap("Green", "#008000");
        addBidirectionalMap("GreenYellow", "#ADFF2F");
        addBidirectionalMap("HoneyDew", "#F0FFF0");
        addBidirectionalMap("HotPink", "#FF69B4");
        addBidirectionalMap("IndianRed", "#CD5C5C");
        addBidirectionalMap("Indigo", "#4B0082");
        addBidirectionalMap("Ivory", "#FFFFF0");
        addBidirectionalMap("Khaki", "#F0E68C");
        addBidirectionalMap("Lavender", "#E6E6FA");
        addBidirectionalMap("LavenderBlush", "#FFF0F5");
        addBidirectionalMap("LawnGreen", "#7CFC00");
        addBidirectionalMap("LemonChiffon", "#FFFACD");
        addBidirectionalMap("LightBlue", "#ADD8E6");
        addBidirectionalMap("LightCoral", "#F08080");
        addBidirectionalMap("LightCyan", "#E0FFFF");
        addBidirectionalMap("LightGoldenRodYellow", "#FAFAD2");
        addBidirectionalMap("LightGray", "#D3D3D3"); // Also LightGrey
        addBidirectionalMap("LightGreen", "#90EE90");
        addBidirectionalMap("LightPink", "#FFB6C1");
        addBidirectionalMap("LightSalmon", "#FFA07A");
        addBidirectionalMap("LightSeaGreen", "#20B2AA");
        addBidirectionalMap("LightSkyBlue", "#87CEFA");
        addBidirectionalMap("LightSlateGray", "#778899"); // Also LightSlateGrey
        addBidirectionalMap("LightSteelBlue", "#B0C4DE");
        addBidirectionalMap("LightYellow", "#FFFFE0");
        addBidirectionalMap("Lime", "#00FF00"); // Same as Green? No, Lime is #00FF00, Green is #008000
        addBidirectionalMap("LimeGreen", "#32CD32");
        addBidirectionalMap("Linen", "#FAF0E6");
        addBidirectionalMap("Magenta", "#FF00FF"); // Same as Fuchsia
        addBidirectionalMap("Maroon", "#800000");
        addBidirectionalMap("MediumAquaMarine", "#66CDAA");
        addBidirectionalMap("MediumBlue", "#0000CD");
        addBidirectionalMap("MediumOrchid", "#BA55D3");
        addBidirectionalMap("MediumPurple", "#9370DB");
        addBidirectionalMap("MediumSeaGreen", "#3CB371");
        addBidirectionalMap("MediumSlateBlue", "#7B68EE");
        addBidirectionalMap("MediumSpringGreen", "#00FA9A");
        addBidirectionalMap("MediumTurquoise", "#48D1CC");
        addBidirectionalMap("MediumVioletRed", "#C71585");
        addBidirectionalMap("MidnightBlue", "#191970");
        addBidirectionalMap("MintCream", "#F5FFFA");
        addBidirectionalMap("MistyRose", "#FFE4E1");
        addBidirectionalMap("Moccasin", "#FFE4B5");
        addBidirectionalMap("NavajoWhite", "#FFDEAD");
        addBidirectionalMap("Navy", "#000080");
        addBidirectionalMap("OldLace", "#FDF5E6");
        addBidirectionalMap("Olive", "#808000");
        addBidirectionalMap("OliveDrab", "#6B8E23");
        addBidirectionalMap("Orange", "#FFA500");
        addBidirectionalMap("OrangeRed", "#FF4500");
        addBidirectionalMap("Orchid", "#DA70D6");
        addBidirectionalMap("PaleGoldenRod", "#EEE8AA");
        addBidirectionalMap("PaleGreen", "#98FB98");
        addBidirectionalMap("PaleTurquoise", "#AFEEEE");
        addBidirectionalMap("PaleVioletRed", "#DB7093");
        addBidirectionalMap("PapayaWhip", "#FFEFD5");
        addBidirectionalMap("PeachPuff", "#FFDAB9");
        addBidirectionalMap("Peru", "#CD853F");
        addBidirectionalMap("Pink", "#FFC0CB");
        addBidirectionalMap("Plum", "#DDA0DD");
        addBidirectionalMap("PowderBlue", "#B0E0E6");
        addBidirectionalMap("Purple", "#800080");
        addBidirectionalMap("RebeccaPurple", "#663399");
        addBidirectionalMap("Red", "#FF0000");
        addBidirectionalMap("RosyBrown", "#BC8F8F");
        addBidirectionalMap("RoyalBlue", "#4169E1");
        addBidirectionalMap("SaddleBrown", "#8B4513");
        addBidirectionalMap("Salmon", "#FA8072");
        addBidirectionalMap("SandyBrown", "#F4A460");
        addBidirectionalMap("SeaGreen", "#2E8B57");
        addBidirectionalMap("SeaShell", "#FFF5EE");
        addBidirectionalMap("Sienna", "#A0522D");
        addBidirectionalMap("Silver", "#C0C0C0");
        addBidirectionalMap("SkyBlue", "#87CEEB");
        addBidirectionalMap("SlateBlue", "#6A5ACD");
        addBidirectionalMap("SlateGray", "#708090"); // Also SlateGrey
        addBidirectionalMap("Snow", "#FFFAFA");
        addBidirectionalMap("SpringGreen", "#00FF7F");
        addBidirectionalMap("SteelBlue", "#4682B4");
        addBidirectionalMap("Tan", "#D2B48C");
        addBidirectionalMap("Teal", "#008080");
        addBidirectionalMap("Thistle", "#D8BFD8");
        addBidirectionalMap("Tomato", "#FF6347");
        addBidirectionalMap("Turquoise", "#40E0D0");
        addBidirectionalMap("Violet", "#EE82EE");
        addBidirectionalMap("Wheat", "#F5DEB3");
        addBidirectionalMap("White", "#FFFFFF");
        addBidirectionalMap("WhiteSmoke", "#F5F5F5");
        addBidirectionalMap("Yellow", "#FFFF00");
        addBidirectionalMap("YellowGreen", "#9ACD32");

        // Handle common aliases explicitly if necessary (e.g. Grey vs Gray)
        HEX_TO_NAME.put("#A9A9A9", "DarkGray"); // Prefer DarkGray over DarkGrey
        HEX_TO_NAME.put("#2F4F4F", "DarkSlateGray");
        HEX_TO_NAME.put("#696969", "DimGray");
        HEX_TO_NAME.put("#808080", "Gray");
        HEX_TO_NAME.put("#D3D3D3", "LightGray");
        HEX_TO_NAME.put("#778899", "LightSlateGray");
        HEX_TO_NAME.put("#708090", "SlateGray");
        // Magenta and Fuchsia are the same color (#FF00FF)
        HEX_TO_NAME.put("#FF00FF", "Magenta"); // Prefer Magenta
    }

    private static void addBidirectionalMap(String name, String hex) {
        String normalizedHex = hex.toUpperCase();
        HEX_TO_NAME.put(normalizedHex, name);
        NAME_TO_HEX.put(name.toLowerCase(), normalizedHex);
    }

    public static String getNameFromHex(String hexValue) {
        if (hexValue == null) return null;
        return HEX_TO_NAME.get(hexValue.toUpperCase());
    }

    public static String getHexFromName(String name) {
        if (name == null) return null;
        return NAME_TO_HEX.get(name.toLowerCase());
    }

    public static boolean isKnownColorName(String name) {
        if (name == null) return false;
        return NAME_TO_HEX.containsKey(name.toLowerCase());
    }
}
