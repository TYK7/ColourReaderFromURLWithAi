export interface ColorInfo {
  hexValue: string | null; // Can be null if only RGB or name is available
  rgbValue: string | null; // Can be null if only HEX or name is available
  name: string | null;     // Can be the raw value from CSS, a basic mapping, or null
  source: string;
  error?: string; // Optional: For backend to pass specific error messages for a color entry (e.g. image processing failed for one image)
}
