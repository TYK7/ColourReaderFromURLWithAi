import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ColorInfo } from '../color-info'; // Adjust path as necessary

@Injectable({
  providedIn: 'root'
})
export class ColorExtractionApiService {
  private apiUrl = 'http://localhost:8080/api/colors'; // Backend API URL

  constructor(private http: HttpClient) { }

  extractColors(url: string): Observable<ColorInfo[]> {
    if (!url || url.trim() === '') {
      return of([{
        source: 'frontend-validation',
        error: 'URL cannot be empty.',
        hexValue: null,
        rgbValue: null,
        name: null
      }]);
    }

    const params = new HttpParams().set('url', url);

    return this.http.get<ColorInfo[]>(this.apiUrl, { params: params }).pipe(
      map(response => {
        // The backend might return a single ColorInfo object with an error field,
        // e.g., if the URL itself is invalid before any colors are processed.
        // Or it might return a list, where some items could have errors (e.g., one image failed).
        if (Array.isArray(response)) {
          return response;
        }
        // If it's not an array but a single object, and it has an error, wrap it in an array.
        // This handles cases where the Spring controller returns a single error object directly.
        // However, the Spring controller is expected to return List<ColorInfo>.
        // This check is more for robustness if the backend API contract is loose.
        // For our current Spring backend, it should always return a list or an error that HttpErrorResponse handles.
        return response as any; // Assuming it's ColorInfo[] as per <ColorInfo[]> generic
      }),
      catchError((error: HttpErrorResponse) => {
        let userFriendlyErrorMessage = 'An unknown error occurred.';

        if (error.error instanceof ErrorEvent) { // Client-side or network error
          userFriendlyErrorMessage = `Network error: ${error.error.message}`;
          console.error('Network error:', error.error.message);
        } else { // Backend returned an unsuccessful response code
          console.error(`Backend error - Status: ${error.status}, Body:`, error.error);
          if (error.status === 0) { // Special case for network errors that look like backend errors
             userFriendlyErrorMessage = 'Could not connect to the backend. Please check your network or if the server is running.';
          } else if (error.error && Array.isArray(error.error) && error.error.length > 0 && error.error[0].error) {
            // This matches the List<ColorInfo> structure with an error message from RestExceptionHandler
            userFriendlyErrorMessage = error.error[0].error;
          } else if (error.error && typeof error.error === 'string') {
            // Plain string error response
            userFriendlyErrorMessage = error.error;
          } else if (error.statusText) {
            userFriendlyErrorMessage = `Error: ${error.status} - ${error.statusText}`;
          } else {
            userFriendlyErrorMessage = `Backend error: Status ${error.status}.`;
          }
        }

        // Return an observable with a user-facing error message wrapped in ColorInfo structure
        return of([{
          source: error.error instanceof ErrorEvent ? 'client-network-error' : 'backend-error',
          error: userFriendlyErrorMessage,
          hexValue: null,
          rgbValue: null,
          name: null
        } as ColorInfo]);
      })
    );
  }
}
