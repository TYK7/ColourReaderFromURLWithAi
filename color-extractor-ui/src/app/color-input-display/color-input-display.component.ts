import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { CommonModule } from '@angular/common'; // Import CommonModule for *ngIf, *ngFor etc.

interface ColorInfo {
  hexValue?: string;
  rgbValue?: string;
  name?: string;
  source?: string;
}

@Component({
  selector: 'app-color-input-display',
  standalone: true, // Mark as standalone
  imports: [
    FormsModule, // Add FormsModule here
    CommonModule   // Add CommonModule here
  ],
  templateUrl: './color-input-display.component.html',
  styleUrls: ['./color-input-display.component.css']
})
export class ColorInputDisplayComponent {
  url: string = '';
  colors: ColorInfo[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  submitted: boolean = false; // To track if extraction has been attempted

  constructor(private colorApiService: ColorExtractionApiService) { } // Inject the service

  extractColors() {
    this.isLoading = true;
    this.errorMessage = '';
    this.colors = [];
    this.submitted = true; // Mark that a submission attempt was made
    console.log('URL submitted:', this.url);

    if (!this.url || this.url.trim() === '') {
      this.errorMessage = 'URL cannot be empty. Please enter a valid URL.';
      this.isLoading = false;
      this.colors = [];
      return;
    }
    if (!this.isValidUrl(this.url)) {
      this.errorMessage = 'Invalid URL format. Please include http:// or https:// and a valid domain.';
      this.isLoading = false;
      this.colors = [];
      return;
    }

    // Call the service
    this.colorApiService.extractColors(this.url).subscribe({
      next: (data) => {
        this.isLoading = false;
        if (data && data.length > 0 && data[0].error) {
          // This handles errors returned in the ColorInfo structure,
          // including those from frontend validation in service, backend validation (400), or server errors (500)
          // that the service formats into ColorInfo[].
          this.errorMessage = data[0].error;
          this.colors = [];
        } else if (data && data.length === 0) {
          this.colors = [];
          // This is the "No distinct colors found" scenario
          this.errorMessage = 'No distinct colors were found on this page. The page might have no parsable colors or uses complex CSS/JS rendering not supported by this tool.';
        } else {
          this.colors = data;
          this.errorMessage = ''; // Clear any previous error messages
        }
      },
      error: (err) => {
        // This error block in subscribe is now more of a fallback for truly unexpected issues
        // not caught by the service's catchError (e.g. if the service itself has a bug before making the HTTP call).
        // Or if the service's catchError re-throws an error instead of returning an Observable<ColorInfo[]>.
        // Our current service design aims to always return Observable<ColorInfo[]> via of().
        this.isLoading = false;
        this.errorMessage = `An critical client-side error occurred: ${err.message || 'Unknown error. Check console.'}`;
        this.colors = [];
        console.error('Critical error subscribing to colorApiService:', err);
      },
      complete: () => {
        this.isLoading = false; // Ensure loading is turned off in all paths
      }
    });
  }

  private isValidUrl(urlString: string): boolean {
    try {
      const url = new URL(urlString);
      // Check for http or https protocol
      if (url.protocol !== "http:" && url.protocol !== "https:") {
        return false;
      }
      // Check if hostname is present (basic check for domain)
      return !!url.hostname;
    } catch (_) {
      // URL constructor throws if the string is not a valid URL
      return false;
    }
  }
}
