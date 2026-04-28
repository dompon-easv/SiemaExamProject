# Siema Exam Project

## 📖 Overview
**Siema Exam Project** is a robust JavaFX desktop application developed as part of an academic examination (SEA). The system is designed to handle user management and the archival of physical documents into a digital format. It provides a secure, high-performance architecture for processing scanned files, managing document boxes, and handling concurrent data ingestion.

## ✨ Key Features
* **User Management System:** Securely add, edit, and manage system users using unique UUID identifiers to prevent enumeration attacks.
* **Document & Box Archiving:** Organize scanned files into Documents, and group Documents into physical Boxes for perfect digital-to-physical tracking.
* **In-Memory Image Manipulation:** Users can rotate scanned files (0°, 90°, 180°, 270°) using a visual UI. Changes are held securely in memory (Presentation Model) and only committed to the database upon user confirmation.
* **High-Speed File Processing:** Capable of handling rapid, multi-threaded file ingestion from scanning APIs without losing chronological sorting order.

## 🛠️ Technology Stack
* **Frontend:** JavaFX, FXML, CSS
* **Backend:** Java (JDK 17+)
* **Database:** Microsoft SQL Server (MSSQL) via JDBC
* **Architecture:** MVC (Model-View-Controller) with strict Separation of Concerns.

## 🏗️ Architectural Highlights

### 1. Advanced UI Routing (`SceneManager`)
The application implements a custom `SceneManager` service to completely decouple the View transitions from the Business Logic Layer. Controllers handle explicit UI logic (e.g., opening dialogs), while GUI Models (Presentation Models) manage `ObservableList` data and communicate with background services.

### 2. Dual-ID Database Strategy
To resolve concurrency issues during parallel file scanning while maintaining rapid SQL `JOIN` performance, the application utilizes a hybrid database key pattern:
* **`Id` (INT IDENTITY):** Used as the internal database Primary Key for efficient Foreign Key mapping between Users, Boxes, Documents, and Files.
* **`ReferenceId` (BINARY(16)):** A Time-Ordered UUID (v7) generated instantly by the Java API. This guarantees thread-safe chronological sorting and captures the exact "Scanned At" timestamp without requiring additional database columns.
