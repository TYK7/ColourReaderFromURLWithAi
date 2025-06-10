import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient, withFetch } from '@angular/common/http'; // Import provideHttpClient and withFetch

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), // Default Angular 17 zone config
    provideHttpClient(withFetch()) // Add provideHttpClient withFetch for modern fetch-based http client
    // If you need to support interceptors that rely on traditional XHR, you might omit withFetch()
    // or use provideHttpClient(withInterceptorsFromDi()) along with traditional HttpClientModule imports elsewhere.
    // For this project, withFetch() is fine.
  ]
};
