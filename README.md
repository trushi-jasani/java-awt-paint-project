# 🎨 Java AWT Paint Application

A lightweight and feature-rich classic drawing application built using Java's Abstract Window Toolkit (AWT). This project offers a simple yet robust interface for creating and editing raster images, including a comprehensive set of drawing tools and robust undo/redo functionality.

---

## ✨ Features

This application provides the fundamental tools needed for basic image creation and manipulation:

| Category            | Feature          | Description                                                                         |
| :------------------ | :--------------- | :---------------------------------------------------------------------------------- |
| **Drawing Tools**   | **Pen & Eraser** | Freehand drawing and precise line erasing with adjustable stroke sizes.             |
|                     | **Shapes**       | Dedicated tools for drawing **Rectangles, Squares, Ovals, Circles, and Triangles**. |
|                     | **Fill Tool**    | Classic **Flood Fill** algorithm to quickly color enclosed areas.                   |
|                     | **Text Tool**    | Add text to the canvas with customizable font family, size, and style.              |
| **Editing**         | **Undo/Redo**    | Full history stack management to safely revert or restore actions.                  |
|                     | **Clear Canvas** | Quick function to reset the canvas to the default background color.                 |
| **File Management** | **Save**         | Export the canvas content as a standard **PNG image file**.                         |

---

## 🛠️ Technical Deep Dive

During development, two common challenges inherent to Java AWT were identified and solved:

1.  **Flickering Reduction (Double Buffering)**

    - **Problem:** Excessive blinking occurred during drawing operations due to the screen being cleared and redrawn multiple times in sequence.
    - **Solution:** Implemented the standard AWT double-buffering technique by overriding the `update(Graphics g)` method in `PaintCanvas.java`. This ensures all drawing is done to an off-screen buffer before being painted to the screen in a single, smooth operation.

2.  **Input & Resizing Stability**
    - **Problem:** The right side of the drawing canvas became unresponsive to mouse input after resizing the application window.
    - **Solution:** Introduced the `ensureImageMatchesCanvasSize()` mechanism in `PaintCanvas.java`. This dynamically resizes the internal `BufferedImage` (the drawing buffer) whenever the AWT Canvas component changes size, ensuring the entire visible area is active and drawable.

---

## 🚀 Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- **Java Development Kit (JDK) 8 or higher**

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone [Your Repository URL]
    ```
2.  **Compile the source files:**
    Navigate to the project's source directory (e.g., `src/com/paintapp`) and compile the Java files.
    ```bash
    javac com/paintapp/*.java
    ```
3.  **Run the Main class:**
    Execute the application from the root of your compiled classes (or the output directory, if using an IDE).
    ```bash
    java com.paintapp.Main
    ```

---

## ⚙️ Project Structure (Relevant Files)

| File                | Role                                                                                                                                             |
| :------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------- |
| `Main.java`         | Contains the entry point and sets up the main `Frame` layout using `BorderLayout`.                                                               |
| `PaintCanvas.java`  | **Core Drawing Logic.** Handles all painting, input events (`MouseListener`), and state management (undo/redo, tool selection, rendering fixes). |
| `ToolbarPanel.java` | Builds the top panel UI with buttons, selectors (stroke, font), and color swatches.                                                              |
| `Tool.java`         | Defines the `enum` for all available drawing tools.                                                                                              |
| `Constants.java`    | Stores global application constants (e.g., canvas size, background color).                                                                       |

---

## ✍️ Author

- **[Your Name / GitHub Username]** - _Initial development_
  - [Link to your GitHub profile or Portfolio]
