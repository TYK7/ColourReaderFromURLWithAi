package com.example.colorschemeidentifier.model;

import lombok.Data;

@Data
public class ColorFrequencyInfo {
    private ColorInfo colorInfo;
    private int frequency;

    public ColorFrequencyInfo(ColorInfo colorInfo) {
        this.colorInfo = colorInfo;
        this.frequency = 1;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    // Delegate methods to ColorInfo for convenience if needed, or access via getColorInfo()
    public String getHexValue() { return colorInfo.getHexValue(); }
    public String getRgbValue() { return colorInfo.getRgbValue(); }
    public String getName() { return colorInfo.getName(); }
    public String getSource() { return colorInfo.getSource(); }
}
