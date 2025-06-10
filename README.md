# Website Color Scheme Identifier

This project is a web application that allows users to extract color schemes from websites.
The frontend is built with Angular, and the backend is a Spring Boot API.

## Features

*   Extracts colors from website HTML (inline styles), CSS (linked stylesheets), and images.
*   Identifies dominant colors from images.
*   Counts color frequencies and returns the top N (currently 20) most frequent colors.
*   Provides HEX, RGB, and friendly names for extracted colors (using a standard list of 140 web colors).
*   Simple web UI to input a URL and view the extracted color palette.

## Project Structure

The project is organized into two main parts:

*   The root directory contains the Java Spring Boot backend application.
*   `color-extractor-ui/`: The Angular frontend application.

## Prerequisites

*   Java JDK (version 11 or higher, as specified in `pom.xml`)
*   Maven (for building the backend)
*   Node.js and npm (for building and running the frontend, Node v18+ used in development)
*   Angular CLI (version 17 used, `npm install -g @angular/cli@17`)

## Running the Application

### 1. Backend (Spring Boot)

1.  **Navigate to the project root directory** (this is where `pom.xml` is located).
2.  **Build the project using Maven:**
    ```bash
    ./mvnw clean package
    # or `mvn clean package` if you have Maven installed globally
    ```
3.  **Run the application:**
    ```bash
    java -jar target/colorschemeidentifier-0.0.1-SNAPSHOT.jar
    ```
    The backend API will start on `http://localhost:8080`.

### 2. Frontend (Angular)

1.  **Navigate to the frontend directory:**
    ```bash
    cd color-extractor-ui
    ```
2.  **Install dependencies (if you haven't already):**
    ```bash
    npm install
    ```
3.  **Run the Angular development server:**
    ```bash
    ng serve
    ```
    The frontend application will be available at `http://localhost:4200`.

### Using the Application

1.  Open your browser and go to `http://localhost:4200`.
2.  Enter a full website URL (including `http://` or `https://`) into the input field.
3.  Click "Extract Colors".
4.  The application will display the top extracted colors from the website.

## API Endpoint

The backend exposes the following endpoint:

*   **GET `/api/colors`**
    *   **Query Parameter:** `url` (String, required) - The URL of the website to analyze.
    *   **Success Response (200 OK):** Returns a JSON array of `ColorInfo` objects, sorted by frequency.
        ```json
        [
          {
            "hexValue": "#RRGGBB",
            "rgbValue": "rgb(r, g, b)",
            "name": "Friendly Color Name",
            "source": "css_file:style.css"
          }
        ]
        ```
    *   **Error Responses:**
        *   `400 Bad Request`: If the URL parameter is missing or invalid. The body will be a `List<ColorInfo>` containing a single error object (e.g., `[{"hexValue":null,"rgbValue":null,"name":"URL parameter cannot be empty.","source":"input_validation"}]`).
        *   `500 Internal Server Error`: For other server-side issues. The body will also be a `List<ColorInfo>` containing a single error object.

## Notes
* The application processes a limited number of images (top 10) and images up to a certain size (5MB) to ensure performance.
* Color extraction from dynamic content loaded by JavaScript after initial page load might be limited as the backend primarily parses static HTML and CSS.
* The color names are based on a standard list of 140 web colors. Colors not on this list (e.g., from images) will typically have their HEX value as their name.
```
