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

### 🖌️ Drawing and Creation

| Feature           | Description                                                                         |
| :---------------- | :---------------------------------------------------------------------------------- |
| **Pen & Eraser**  | Freehand drawing and line erasing with **adjustable stroke sizes**.                 |
| **Shapes**        | Dedicated tools for drawing **Rectangles, Squares, Ovals, Circles, and Triangles**. |
| **Fill Tool**     | Classic **Flood Fill** algorithm to quickly color enclosed shapes.                  |
| **Text Tool**     | Add text to your canvas with customizable **font family, size, and style**.         |
| **Color Palette** | A wide selection of **color swatches** for quick color changes.                     |

### 💾 Editing and Management

| Feature          | Description                                                                      |
| :--------------- | :------------------------------------------------------------------------------- |
| **Undo/Redo**    | Full history stack management to safely **revert or restore** your last actions. |
| **Clear Canvas** | Quick function to reset the canvas to a clean slate.                             |
| **Save**         | Export your artwork as a standard **PNG image file**.                            |

---

## 🚀 Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- **Java Development Kit (JDK) 8 or higher**

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/trushi-jasani/java-awt-paint-project.git
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

## 📧 Contact

💁‍♀️ **Author:** Trushi Jasani  
📩 **Email:** jasanitrushi@gmail.com  
🔗 **GitHub:** [trushi-jasani](https://github.com/trushi-jasani)
