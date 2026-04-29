# Obsidian Flow - Project Management System

##  Project Description

**Obsidian Flow** is a comprehensive desktop-based project management application designed to streamline team collaboration, task tracking, and project organization. Built with Java and JavaFX, it provides a robust backend service layer coupled with an intuitive graphical user interface (GUI) for managing projects, tasks, team members, and permissions.

This system is ideal for teams looking for a local, standalone solution to manage their projects without reliance on cloud-based services. It emphasizes data persistence, security through authentication and role-based access control, and comprehensive activity logging.

---

##  Key Features

### Authentication & User Management
- **User Registration & Login**: Secure authentication with encrypted password storage
- **User Profiles**: Manage user information (name, email)
- **Role-Based Permissions**: Support for multiple permission levels (Admin, Project Manager, Member)
- **Activity Logging**: Track all user actions for accountability and audit trails

### Project Management
- **Create & Manage Projects**: Organize work into structured projects with descriptions
- **Project Dashboards**: View project overview with key metrics and statistics
- **Team Member Management**: Add/remove team members and assign roles
- **Project Visibility**: Role-based access control for project viewing and modification

### Task Management
- **Task Creation & Assignment**: Create tasks with detailed descriptions and assign to team members
- **Task Status Tracking**: Monitor task progress (e.g., Pending, In Progress, Completed)
- **Priority Levels**: Set task priority for better organization
- **Due Dates**: Set deadlines and track task timelines
- **Task Comments**: Add comments and discussions to tasks for better collaboration
- **Task Detail View**: Comprehensive task information in dedicated views

### Collaboration & Communication
- **Inline Comments**: Comment on tasks for team discussion
- **Notifications**: Get notified of task assignments and project updates
- **Team Dashboard**: View all team activities and updates in one place

### Reporting & Analytics
- **Reports**: Generate reports on project progress and task completion
- **Activity Logs**: View detailed activity logs of all system operations
- **Settings**: Configure application preferences and user settings

### Data Persistence
- **SQLite Database**: Local database for reliable data persistence
- **Relational Data Model**: Properly normalized database structure for data integrity

---

## Technology Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Java 21 |
| **Frontend Framework** | JavaFX 21.0.1 |
| **UI Markup** | FXML |
| **Database** | SQLite 3.45.0 |
| **Build Tool** | Maven 4.0.0 |
| **Testing Framework** | JUnit 5 (Jupiter) |
| **Mocking Framework** | Mockito 5.5.0 |

---

## Project Structure

```
obsidian-flow/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Main.java                          # Application entry point
│   │   │   ├── backend/
│   │   │   │   ├── database/                      # Database layer
│   │   │   │   │   ├── Database.java
│   │   │   │   │   └── Constants.java
│   │   │   │   ├── models/                        # Data models
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Project.java
│   │   │   │   │   ├── Task.java
│   │   │   │   │   ├── Comment.java
│   │   │   │   │   ├── ActivityLog.java
│   │   │   │   │   ├── ProjectDashboard.java
│   │   │   │   │   └── User.java
│   │   │   │   ├── repositories/                  # Data access layer
│   │   │   │   │   ├── UserRepo.java
│   │   │   │   │   ├── ProjectRepo.java
│   │   │   │   │   ├── TaskRepo.java
│   │   │   │   │   ├── CommentRepo.java
│   │   │   │   │   ├── ActivityLogRepo.java
│   │   │   │   │   └── RepositoryBase.java
│   │   │   │   ├── services/                      # Business logic layer
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── ProjectService.java
│   │   │   │   │   ├── TaskService.java
│   │   │   │   │   └── CommentService.java
│   │   │   │   ├── enums/                         # Enumerations
│   │   │   │   │   ├── Permissions.java
│   │   │   │   │   └── TaskStatus.java
│   │   │   │   ├── interfaces/                    # Interfaces
│   │   │   │   │   └── Repository.java
│   │   │   │   └── util/                          # Utilities
│   │   │   └── frontend/
│   │   │       ├── controllers/                   # JavaFX controllers
│   │   │       ├── models/                        # Frontend models
│   │   │       └── utils/                         # Frontend utilities
│   │   └── resources/com/example/
│   │       ├── fxml/                              # FXML UI files
│   │       │   ├── login.fxml
│   │       │   ├── signup.fxml
│   │       │   ├── dashboard.fxml
│   │       │   ├── projects.fxml
│   │       │   ├── tasks.fxml
│   │       │   ├── CreateProject.fxml
│   │       │   ├── addTask.fxml
│   │       │   ├── TaskDetail.fxml
│   │       │   ├── MemberDashboard.fxml
│   │       │   ├── Team.fxml
│   │       │   ├── Reports.fxml
│   │       │   ├── Notifications.fxml
│   │       │   ├── Settings.fxml
│   │       │   └── AddMemberDialog.fxml
│   │       └── images/                            # Application images & assets
│   └── test/
│       └── java/com/example/
│           └── backend/
│               ├── models/                        # Model unit tests
│               ├── repositories/                  # Repository integration tests
│               ├── services/                      # Service layer tests
│               └── testsupport/                   # Test utilities and fixtures
├── pom.xml                                        # Maven configuration
├── SOFTWARE_TEST_PLAN.md                          # Comprehensive testing strategy
└── README.md                                      # This file
```

---

## How to Run

### Prerequisites

- **Java Development Kit (JDK) 21+**: Ensure Java 21 or higher is installed
- **Maven 3.6+**: Required for building the project
- **Git** (optional): For cloning the repository

### Installation & Setup

1. **Clone or Download the Repository**
   ```bash
   cd obsidian-flow
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```
   This command will:
   - Clean any previous build artifacts
   - Download all dependencies (JavaFX, SQLite, JUnit, Mockito)
   - Compile the source code
   - Run all tests
   - Package the application

3. **Run the Application**
   ```bash
   mvn javafx:run
   ```
   This will start the Obsidian Flow application and display the login screen.

   Alternatively, you can run it directly with:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.Main"
   ```

### Running Tests

Execute the comprehensive test suite:

```bash
mvn test
```

This will run all unit tests across:
- **Model Layer Tests**: Validation of data models
- **Repository Layer Tests**: Data access and persistence
- **Service Layer Tests**: Business logic verification

### Viewing Test Reports

After running tests, view the detailed test report:

```bash
mvn surefire-report:report
```

Test reports are generated in: `target/site/surefire-report.html`

---

## Usage Guide

### First Time Use

1. **Launch the Application**: Run the application using one of the methods above
2. **Create an Account**: Click "Sign Up" to register a new account
3. **Login**: Use your credentials to log in
4. **Create a Project**: Click "Create Project" and provide project details
5. **Add Team Members**: Invite team members to your project
6. **Create Tasks**: Start creating and assigning tasks to team members

### Main Features Access

| Feature | How to Access |
|---------|---------------|
| Dashboard | Main screen after login |
| Projects | "Projects" menu item |
| Tasks | "Tasks" menu item or within project |
| Team Management | "Team" menu or within project settings |
| Reports | "Reports" menu |
| Notifications | Bell icon in top menu |
| Settings | "Settings" menu |

---

## Architecture Overview

### Layered Architecture

```
┌─────────────────────────────────────┐
│   Presentation Layer (JavaFX GUI)   │
│   • Controllers                     │
│   • FXML Files                      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Business Logic Layer (Services)   │
│   • AuthService                     │
│   • ProjectService                  │
│   • TaskService                     │
│   • CommentService                  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Data Access Layer (Repositories)  │
│   • UserRepo                        │
│   • ProjectRepo                     │
│   • TaskRepo                        │
│   • CommentRepo                     │
│   • ActivityLogRepo                 │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Data Layer (SQLite Database)      │
│   • Tables & Relationships          │
│   • Persistence                     │
└─────────────────────────────────────┘
```

### Design Patterns Used

- **Model-View-Controller (MVC)**: Separation of concerns in the UI layer
- **Repository Pattern**: Abstract data access logic
- **Service Layer Pattern**: Encapsulate business logic
- **Factory Pattern**: Object creation in repositories
- **Singleton Pattern**: Database connection management

---

## Testing

The project includes comprehensive testing with:

- **Unit Tests**: Validate individual component behavior
- **Integration Tests**: Test interaction between layers
- **Black Box Testing**: Specification-based testing
- **White Box Testing**: Code coverage-based testing

Test coverage includes:
- Authentication workflows
- Project and task operations
- Permission validation
- Data persistence
- Error handling and edge cases

For detailed testing information, see [SOFTWARE_TEST_PLAN.md](SOFTWARE_TEST_PLAN.md).

---

## Database Schema

The SQLite database includes the following main tables:

- **users**: Stores user information and credentials
- **projects**: Project data with manager information
- **tasks**: Task details with assignments and status
- **comments**: Comments on tasks for collaboration
- **activity_logs**: Audit trail of system operations
- **permissions**: User role and permission mappings

---

## Security Features

- **Password Encryption**: User passwords are encrypted before storage
- **Role-Based Access Control**: Different permission levels for users
- **Activity Logging**: All operations are logged for audit trails
- **Input Validation**: Data validation at service and model layers
- **Authentication**: Secure user login and session management

---