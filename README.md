# Siema Exam Project

## 📖 Overview
**Siema Exam Project** is a robust JavaFX desktop application developed as part of an academic examination. The system handles user management and the archival of physical documents into a digital format, providing a secure, high-performance architecture for processing scanned files and managing document boxes.

## ✨ Key Features
* **User Management System:** Secure management of users using unique UUID identifiers and hashed passwords.
* **Document & Box Archiving:** Digital-to-physical tracking by organizing scanned files into Documents and Documents into Boxes.
* **In-Memory Image Manipulation:** Visual rotation of scanned files (0°, 90°, 180°, 270°) with a Presentation Model that only commits changes upon confirmation.
* **High-Speed File Processing:** Multi-threaded file ingestion from scanning APIs while maintaining chronological order.

## 🛠️ Technology Stack
* **Language:** Java 25 (JDK)
* **Frontend:** JavaFX 21, FXML, CSS
* **Database:** Microsoft SQL Server (MSSQL) via JDBC
* **Build Tool:** Maven

## 🏗️ Architecture & Design

### Layered Architecture
The project follows a strict separation of concerns to ensure maintainability and testability:
* **BE (Business Entities):** Pure POJOs representing the core data models.
* **BLL (Business Logic Layer):** Encapsulates business rules, validation, and security (e.g., `UserService`, `ClientProfileService`).
* **DAL (Data Access Layer):** Handles all database interactions using the **DAO Pattern** (`IUserDAO` $\rightarrow$ `UserDAO`).
* **GUI (Graphical User Interface):** Implements an **MVC/MVVM** approach using JavaFX controllers and dedicated Models to manage UI state.

### Design Patterns Used
* **DAO (Data Access Object):** Abstracts database logic from business logic.
* **Service Layer:** Acts as a facade between the GUI and the data layer.
* **Presentation Model:** Separates the raw data entities from the data required by the UI (e.g., `AdminModel`).
* **Dependency Injection:** Manual injection of services via `ApplicationServices` for better modularity.

### Advanced Architectural Highlights
* **Custom UI Routing:** A `SceneManager` decouples view transitions from business logic.
* **Dual-ID Database Strategy:** Uses a hybrid approach with `INT IDENTITY` for internal performance and `BINARY(16)` Time-Ordered UUIDs (v7) for thread-safe chronological sorting.

## 🚀 Getting Started

### Prerequisites
* Java 25 SDK
* Maven
* Microsoft SQL Server

### Configuration
1. Navigate to the `config/` folder.
2. Edit `config.settings` with your database credentials:
   ```properties
   Server=your_server
   Database=your_db
   User=your_user
   Password=your_password
   Port=1433
   ```

### Running the Application
Using Maven:
```bash
mvn clean compile exec:java -Dexec.mainClass="dk.siema.siemaexamproject.app.Launcher"
```
