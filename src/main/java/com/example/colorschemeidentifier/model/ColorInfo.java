package com.example.colorschemeidentifier.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode // To ensure uniqueness based on all fields
public class ColorInfo {
    private String hexValue;
    private String rgbValue;
    private String name;
    private String source;
    private boolean isLogoColor;

    public ColorInfo() {
        // Default constructor
    }

    public ColorInfo(String hexValue, String rgbValue, String name, String source) {
        this.hexValue = hexValue;
        this.rgbValue = rgbValue;
        this.name = name;
        this.source = source;
        this.isLogoColor = false; // Default to false
    }

    public ColorInfo(String hexValue, String rgbValue, String name, String source, boolean isLogoColor) {
        this.hexValue = hexValue;
        this.rgbValue = rgbValue;
        this.name = name;
        this.source = source;
        this.isLogoColor = isLogoColor;
    }

    // toString() might be useful for debugging, Lombok's @Data includes it.
}
