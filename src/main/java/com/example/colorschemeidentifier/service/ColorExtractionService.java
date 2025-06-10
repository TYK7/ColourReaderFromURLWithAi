package com.example.colorschemeidentifier.service;

import com.example.colorschemeidentifier.model.ColorFrequencyInfo;
import com.example.colorschemeidentifier.model.ColorInfo;
import com.example.colorschemeidentifier.util.ColorNamer; // Import ColorNamer
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// import java.io.ByteArrayOutputStream; // No longer strictly needed after refactor
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
// import java.util.Collections; // No longer strictly needed
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet; // Still used for imageUrls
import java.util.List;
import java.util.Map;
import java.util.Set; // Still used for supported image extensions
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ColorExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(ColorExtractionService.class);

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
    private static final Pattern RGB_COLOR_PATTERN = Pattern.compile("rgba?\\((\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d{1,3})(,\\s*\\d*\\.?\\d+)?\\)");
    private static final String[] COLOR_PROPERTIES = {"color", "background-color", "border-color", "fill", "stroke", "background"};
    private static final Pattern CSS_URL_PATTERN = Pattern.compile("url\\(['\"]?([^'\"]+)['\"]?\\)");

    private static final int MAX_IMAGES_TO_PROCESS = 10;
    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final int TOP_N_COLORS = 20;

    private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp");

    public List<ColorInfo> extractColorsFromUrl(String urlString) {
        Map<String, ColorFrequencyInfo> colorFrequencies = new HashMap<>();

        try {
            Document doc = Jsoup.connect(urlString).timeout(10000).get();
            String baseUrl = doc.baseUri();

            extractColorsFromInlineStyles(doc, colorFrequencies, urlString, baseUrl);
            extractColorsFromCssFiles(doc, colorFrequencies, baseUrl);

            Set<String> imageUrls = collectImageUrls(doc, baseUrl);
            logger.info("Found {} unique image URLs to analyze.", imageUrls.size());
            extractColorsFromImages(imageUrls, colorFrequencies);

        } catch (IOException e) {
            logger.error("Error fetching or parsing URL {}: {}", urlString, e.getMessage());
            return List.of(new ColorInfo(null, null, "Error processing URL: " + e.getMessage(), "system_error"));
        } catch (Exception e) {
            logger.error("An unexpected error occurred while processing URL {}: {}", urlString, e.getMessage(), e);
            return List.of(new ColorInfo(null, null, "Unexpected error: " + e.getMessage(), "system_error"));
        }

        List<ColorFrequencyInfo> sortedByFrequency = colorFrequencies.values().stream()
                .sorted(Comparator.comparingInt(ColorFrequencyInfo::getFrequency).reversed())
                .limit(TOP_N_COLORS)
                .collect(Collectors.toList());

        // TODO: Research and integrate advanced color naming here.

        return sortedByFrequency.stream()
                                .map(ColorFrequencyInfo::getColorInfo)
                                .collect(Collectors.toList());
    }

    private void addOrUpdateColorFrequency(Map<String, ColorFrequencyInfo> colorFrequencies, ColorInfo colorInfo) {
        if (colorInfo == null) return;

        String key = null;
        String name = colorInfo.getName();
        String hex = colorInfo.getHexValue();
        String rgb = colorInfo.getRgbValue();

        if (hex != null) {
            key = hex.toUpperCase();
            String knownName = ColorNamer.getNameFromHex(key);
            if (knownName != null) name = knownName; // Update name if a standard name exists for this hex
        } else if (rgb != null) { // No HEX, try RGB
            key = rgb; // Use RGB as key if hex is not available
            // Potentially try to convert RGB to HEX here if a reliable converter is available
            // and then check ColorNamer.getNameFromHex again. For now, RGB is key.
        } else if (name != null) { // No HEX or RGB, try name
            String knownHex = ColorNamer.getHexFromName(name);
            if (knownHex != null) {
                key = knownHex.toUpperCase();
                hex = key; // Got a hex from a known name
                rgb = convertHexToRgb(hex); // Convert this hex to RGB
                name = ColorNamer.getNameFromHex(key); // Get canonical name
            } else {
                // Name is not a known color name, and no HEX/RGB. Cannot form a good key.
                logger.debug("Skipping color with unknown name and no HEX/RGB: {}", name);
                return;
            }
        }

        if (key == null) {
             logger.debug("Skipping color with insufficient data: {}", colorInfo);
             return;
        }

        // Ensure ColorInfo has the most accurate info we've derived
        final ColorInfo updatedColorInfo = new ColorInfo(hex, rgb, name, colorInfo.getSource());

        ColorFrequencyInfo cfInfo = colorFrequencies.get(key);
        if (cfInfo == null) {
            colorFrequencies.put(key, new ColorFrequencyInfo(updatedColorInfo));
        } else {
            // Optionally, update the ColorInfo in cfInfo if the new one is "better" (e.g., has a source from CSS rather than image)
            // For now, just increment frequency. The first ColorInfo encountered for a key is kept.
            cfInfo.incrementFrequency();
        }
    }

    private void extractColorsFromInlineStyles(Document doc, Map<String, ColorFrequencyInfo> colorFrequencies, String pageSourceUrl, String baseUrl) {
        Elements elementsWithStyle = doc.select("[style]");
        for (Element element : elementsWithStyle) {
            String styleAttribute = element.attr("style");
            findColorsInText(styleAttribute, "inline_style_attribute:" + pageSourceUrl, colorFrequencies, baseUrl);
        }
    }

    private void extractColorsFromCssFiles(Document doc, Map<String, ColorFrequencyInfo> colorFrequencies, String baseUrl) {
        Elements cssLinks = doc.select("link[rel=stylesheet]");
        for (Element link : cssLinks) {
            String cssUrl = link.absUrl("href");
            if (cssUrl.isEmpty()) continue;
            try {
                logger.info("Fetching CSS from: {}", cssUrl);
                String cssContent = Jsoup.connect(cssUrl).timeout(5000).ignoreContentType(true).execute().body();
                findColorsInText(cssContent, "css_file:" + cssUrl, colorFrequencies, cssUrl);
            } catch (IOException e) {
                logger.error("Error fetching or parsing CSS file {}: {}", cssUrl, e.getMessage());
            } catch (Exception e) {
                logger.error("An unexpected error occurred while processing CSS file {}: {}", cssUrl, e.getMessage(), e);
            }
        }
    }

    private void findColorsInText(String text, String source, Map<String, ColorFrequencyInfo> colorFrequencies, String contextUrl) {
        Matcher hexMatcher = HEX_COLOR_PATTERN.matcher(text);
        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(0).toUpperCase();
            String rgbFromHex = convertHexToRgb(hexColor);
            addOrUpdateColorFrequency(colorFrequencies, new ColorInfo(hexColor, rgbFromHex, hexColor, source));
        }

        Matcher rgbMatcher = RGB_COLOR_PATTERN.matcher(text);
        while (rgbMatcher.find()) {
            String rgbColor = rgbMatcher.group(0);
            addOrUpdateColorFrequency(colorFrequencies, new ColorInfo(null, rgbColor, rgbColor, source));
        }

        String[] declarations = text.split(";");
        for (String declaration : declarations) {
            String[] parts = declaration.split(":", 2);
            if (parts.length < 2) continue;
            String propertyName = parts[0].trim().toLowerCase();
            String propertyValue = parts[1].trim();
            if (isColorProperty(propertyName)) {
                String cleanedValue = propertyValue.split(" ")[0].replaceAll("!important", "").replaceAll("\"", "").replaceAll("'", "").trim();
                String hexFromName = ColorNamer.getHexFromName(cleanedValue);
                if (hexFromName != null) { // It's a known color name
                    String canonicalName = ColorNamer.getNameFromHex(hexFromName); // Get the canonical casing
                    addOrUpdateColorFrequency(colorFrequencies, new ColorInfo(hexFromName, convertHexToRgb(hexFromName), canonicalName, source));
                } else {
                    // Value is not a known name, could be a HEX/RGB already handled or something else.
                    // The HEX/RGB regexes above would have caught it if it were a direct HEX/RGB.
                    // This path is for named colors primarily.
                }
            }
        }
    }

    private Set<String> collectImageUrls(Document doc, String baseUrl) {
        Set<String> imageUrls = new HashSet<>();
        Elements imgTags = doc.select("img[src]");
        for (Element img : imgTags) {
            String absUrl = img.absUrl("src");
            if (!absUrl.isEmpty() && isSupportedImageUrl(absUrl)) imageUrls.add(absUrl);
        }
        Elements elementsWithStyle = doc.select("[style]");
        for (Element element : elementsWithStyle) {
            Matcher urlMatcher = CSS_URL_PATTERN.matcher(element.attr("style"));
            while (urlMatcher.find()) {
                try {
                    String absUrl = new URL(new URL(baseUrl), urlMatcher.group(1)).toString();
                    if (isSupportedImageUrl(absUrl)) imageUrls.add(absUrl);
                } catch (java.net.MalformedURLException e) { logger.warn("Malformed URL from inline style: {} (base: {}, path: {})", e.getMessage(), baseUrl, urlMatcher.group(1)); }
            }
        }
        Elements styleTags = doc.select("style");
        for (Element styleTag : styleTags) {
            Matcher urlMatcher = CSS_URL_PATTERN.matcher(styleTag.html());
            while (urlMatcher.find()) {
                 try {
                    String absUrl = new URL(new URL(baseUrl), urlMatcher.group(1)).toString();
                     if (isSupportedImageUrl(absUrl)) imageUrls.add(absUrl);
                } catch (java.net.MalformedURLException e) { logger.warn("Malformed URL from <style> tag: {} (base: {}, path: {})", e.getMessage(), baseUrl, urlMatcher.group(1));}
            }
        }
        Elements cssLinks = doc.select("link[rel=stylesheet]");
        for (Element link : cssLinks) {
            String cssAbsUrl = link.absUrl("href");
            if (cssAbsUrl.isEmpty()) continue;
            try {
                String cssContent = Jsoup.connect(cssAbsUrl).timeout(5000).ignoreContentType(true).execute().body();
                Matcher urlMatcher = CSS_URL_PATTERN.matcher(cssContent);
                while (urlMatcher.find()) {
                     try {
                        String absUrl = new URL(new URL(cssAbsUrl), urlMatcher.group(1)).toString();
                         if (isSupportedImageUrl(absUrl)) imageUrls.add(absUrl);
                    } catch (java.net.MalformedURLException e) {logger.warn("Malformed URL from CSS file {}: {} (path: {})", cssAbsUrl, e.getMessage(), urlMatcher.group(1));}
                }
            } catch (IOException e) {logger.error("Error fetching CSS for image URL extraction {}: {}", cssAbsUrl, e.getMessage());}
        }
        return imageUrls;
    }

    private boolean isSupportedImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.startsWith("data:image")) {
            String mimeType = lowerUrl.substring(5, lowerUrl.indexOf(';'));
            return SUPPORTED_IMAGE_EXTENSIONS.contains(mimeType.split("/")[1]);
        }
        int lastDot = lowerUrl.lastIndexOf('.');
        if (lastDot == -1 || lastDot == lowerUrl.length() - 1) return false;
        return SUPPORTED_IMAGE_EXTENSIONS.contains(lowerUrl.substring(lastDot + 1));
    }

    private boolean isColorProperty(String propertyName) {
        for (String prop : COLOR_PROPERTIES) if (prop.equals(propertyName)) return true;
        return false;
    }

    // convertHexToRgb can remain as is, it's a utility.
    private String convertHexToRgb(String hexColor) {
        if (hexColor == null) return null;
        String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
        if (hex.length() == 3) hex = "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
        if (hex.length() != 6) return null;
        try {
            return String.format("rgb(%d, %d, %d)", Integer.parseInt(hex.substring(0, 2), 16), Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(hex.substring(4, 6), 16));
        } catch (NumberFormatException e) { return null; }
    }

    // convertColorNameToHex and isBasicColorName are now effectively replaced by ColorNamer methods
    // They can be removed or kept if there's a desire for a minimal fallback for names not in ColorNamer.
    // For now, let's rely on ColorNamer for consistency. These methods will not be used by the main logic.
    /*
    private String convertColorNameToHex(String name) { ... }
    private String convertColorNameToRgb(String name) { ... }
    private boolean isBasicColorName(String name) { ... }
    */


    private void extractColorsFromImages(Set<String> imageUrls, Map<String, ColorFrequencyInfo> colorFrequencies) {
        int processedImageCount = 0;
        for (String imageUrl : imageUrls) {
            if (processedImageCount >= MAX_IMAGES_TO_PROCESS) {
                logger.info("Reached max image processing limit ({}). Skipping remaining images.", MAX_IMAGES_TO_PROCESS);
                break;
            }
            String sourceIdentifier = imageUrl.startsWith("data:image") ? "image_data_uri:" + imageUrl.substring(0, Math.min(imageUrl.length(), 50)) + "..." : imageUrl;
            try {
                InputStream imageStream = imageUrl.startsWith("data:image") ? processDataUriGetStream(imageUrl, sourceIdentifier) : openConnectionAndGetStream(imageUrl);
                if (imageStream == null) continue;

                try (InputStream in = imageStream) { // Ensure stream is closed
                    BufferedImage image = ImageIO.read(in);
                    if (image != null) {
                        extractDominantColorsFromImage(image, colorFrequencies, sourceIdentifier);
                    } else {
                        logger.warn("Could not decode image from source: {}", sourceIdentifier);
                    }
                }
            } catch (IOException e) {
                logger.error("Error reading image stream for {}: {}", sourceIdentifier, e.getMessage());
            } catch (Exception e) {
                logger.error("An unexpected error occurred while processing image {}: {}", sourceIdentifier, e.getMessage(), e);
            }
            processedImageCount++;
        }
    }

    private InputStream processDataUriGetStream(String dataUri, String sourceIdentifier) throws IOException {
        String mimeType = dataUri.substring(dataUri.indexOf(':') + 1, dataUri.indexOf(';'));
        if (!mimeType.startsWith("image/")) {
            logger.warn("Skipping data URI, not an image: {} for source {}", mimeType, sourceIdentifier);
            return null;
        }
        String base64Data = dataUri.substring(dataUri.indexOf(',') + 1);
        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

        if (imageBytes.length > MAX_IMAGE_SIZE_BYTES) {
            logger.warn("Data URI image {} exceeds max size ({} bytes). Skipping.", sourceIdentifier, imageBytes.length);
            return null;
        }
        return new java.io.ByteArrayInputStream(imageBytes);
    }


    private InputStream openConnectionAndGetStream(String imageUrlStr) throws IOException {
        URL url = new URL(imageUrlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("User-Agent", "ColorSchemeIdentifierBot/1.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            logger.warn("Failed to download image {}. Server responded with code: {}", imageUrlStr, responseCode);
            return null;
        }
        String contentType = connection.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.warn("URL {} does not point to a valid image (Content-Type: {}). Skipping.", imageUrlStr, contentType);
            return null;
        }
        int contentLength = connection.getContentLength();
        if (contentLength > MAX_IMAGE_SIZE_BYTES) {
            logger.warn("Image {} exceeds max size ({} bytes reported). Skipping.", imageUrlStr, contentLength);
            return null;
        }
        return connection.getInputStream();
    }

    private void extractDominantColorsFromImage(BufferedImage image, Map<String, ColorFrequencyInfo> colorFrequencies, String sourceUrl) {
        int width = image.getWidth();
        int height = image.getHeight();
        final int MAX_DIMENSION = 100;

        if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
            double scale = Math.min((double)MAX_DIMENSION / width, (double)MAX_DIMENSION / height);
            width = (int)(width * scale);
            height = (int)(height * scale);
            java.awt.Image scaledImage = image.getScaledInstance(width, height, java.awt.Image.SCALE_FAST);
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            newImage.getGraphics().drawImage(scaledImage, 0, 0, null);
            image = newImage;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbVal = image.getRGB(x, y);
                if ((rgbVal >>> 24) < 128) continue; // Skip highly transparent pixels

                int r = (rgbVal >> 16) & 0xFF;
                int g = (rgbVal >> 8) & 0xFF;
                int b = rgbVal & 0xFF;

                int bucketSize = 64;
                int qR = Math.min(255, Math.max(0, (r / bucketSize) * bucketSize + (bucketSize / 2)));
                int qG = Math.min(255, Math.max(0, (g / bucketSize) * bucketSize + (bucketSize / 2)));
                int qB = Math.min(255, Math.max(0, (b / bucketSize) * bucketSize + (bucketSize / 2)));

                String hex = String.format("#%02X%02X%02X", qR, qG, qB);
                String rgbStr = String.format("rgb(%d, %d, %d)", qR, qG, qB);

                addOrUpdateColorFrequency(colorFrequencies, new ColorInfo(hex, rgbStr, hex, "image:" + sourceUrl));
            }
        }
    }
}
