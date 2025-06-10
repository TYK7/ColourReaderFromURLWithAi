package com.example.colorschemeidentifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.colorschemeidentifier.model.ColorInfo;
import com.example.colorschemeidentifier.service.ColorExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin; // Import CrossOrigin
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/api/colors")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular dev server
public class ColorController {

    private final ColorExtractionService colorExtractionService;

    @Autowired
    public ColorController(ColorExtractionService colorExtractionService) {
        this.colorExtractionService = colorExtractionService;
    }

    @GetMapping
    public List<ColorInfo> getColorScheme(@RequestParam String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL parameter cannot be empty.");
        }
        try {
            // Validate URL structure and scheme
            URL validatedUrl = new URL(url);
            if (!"http".equals(validatedUrl.getProtocol()) && !"https".equals(validatedUrl.getProtocol())) {
                throw new IllegalArgumentException("Invalid URL scheme: Only HTTP and HTTPS are supported.");
            }
            // Additional check for syntax if needed, though URL constructor often covers it.
            validatedUrl.toURI(); // Checks for URI syntax validity
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: " + e.getMessage());
        }

        // The service itself might return an empty list or a list with error messages for specific processing issues.
        // For general failures in the service (e.g., can't connect to target URL), those will be handled by ControllerAdvice if they throw RuntimeExceptions.
        return colorExtractionService.extractColorsFromUrl(url);
    }
}
