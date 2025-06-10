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

    public ColorInfo(String hexValue, String rgbValue, String name, String source) {
        this.hexValue = hexValue;
        this.rgbValue = rgbValue;
        this.name = name;
        this.source = source;
    }

    // toString() might be useful for debugging, Lombok's @Data includes it.
}
