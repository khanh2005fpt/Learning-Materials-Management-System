# 📚 Learning Material Management System (LMMS)

<p align="center">
  <img src="docs/logo.png" width="180" alt="LMMS Logo"/>
</p>

<p align="center">
A web-based Learning Material Management System that enables students and lecturers to upload, organize, search, and manage educational resources efficiently using semantic and keyword search powered by Elasticsearch.
</p>

---

# 📖 Table of Contents

* Overview
* Features
* Technology Stack
* System Architecture
* Project Structure
* Installation
* Environment Configuration
* Running the Project
* User Roles
* Core Functionalities
* AI & Search Features
* Database Design
* REST API
* Future Enhancements
* Contributors
* License

---

# 📌 Overview

Learning Material Management System (LMMS) is a web application designed to help educational institutions manage digital learning resources such as lecture notes, books, assignments, slides, and research documents.

The system allows users to upload learning materials, categorize them, search documents by keywords or semantic meaning, and manage resources through a modern web interface.

The project follows a **three-tier architecture** consisting of a React frontend, Spring Boot REST API backend, and MySQL database, with Elasticsearch providing high-performance document search.

---

# ✨ Features

## Authentication

* User Registration
* Secure Login
* JWT Authentication
* Role-Based Authorization
* Password Encryption

---

## User Management

* User Profile
* Update Personal Information
* Change Password
* Role Management (Admin)

---

## Learning Material Management

* Upload PDF Documents
* Download Materials
* Edit Material Information
* Delete Materials
* Material Categories
* Subject Classification
* Author Information

---

## File Management

* Store PDF Files
* Large File Upload Support
* File Validation
* Metadata Extraction

---

## Search System

* Keyword Search
* Full-text Search
* Elasticsearch Indexing
* Search Suggestions
* Highlight Search Results
* Fast Retrieval

---

## Dashboard

* Total Documents
* Total Users
* Categories Overview
* Recently Uploaded Materials

---

# 🚀 Technology Stack

## Frontend

* React
* Vite
* React Bootstrap
* Axios
* React Router DOM

---

## Backend

* Spring Boot 3
* Spring Security
* Spring Data JPA
* Spring MVC
* JWT Authentication
* Apache PDFBox
* Apache Tika

---

## Database

* MySQL

---

## Search Engine

* Elasticsearch

---

## Development Tools

* IntelliJ IDEA
* VS Code
* Postman
* Maven
* Git
* Docker (Optional)

---

# 🏗 System Architecture

```text
                React (Vite)
                      │
                 REST API
                      │
              Spring Boot Backend
            ┌─────────┴─────────┐
            │                   │
        MySQL Database     Elasticsearch
            │                   │
            └──── Document Metadata
                      │
                 PDF Storage
```

---

# 📂 Project Structure

```text
LMMS/

├── backend
│
│   ├── src/main/java
│   │
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── security
│   ├── config
│   └── util
│
│   ├── uploads
│   ├── pom.xml
│   └── application.properties
│
├── frontend
│
│   ├── src
│   │
│   ├── components
│   ├── pages
│   ├── layouts
│   ├── services
│   ├── hooks
│   ├── utils
│   └── App.jsx
│
└── README.md
```

---

# ⚙ Installation

## Clone Repository

```bash
git clone https://github.com/yourusername/lmms.git

cd lmms
```

---

# Backend Setup

```bash
cd backend

mvn clean install
```

Configure **application.properties**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lmms

spring.datasource.username=root

spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

spring.servlet.multipart.max-file-size=50MB

spring.servlet.multipart.max-request-size=50MB

jwt.secret=your_secret_key

elasticsearch.host=localhost

elasticsearch.port=9200
```

Run Spring Boot

```bash
mvn spring-boot:run
```

---

# Frontend Setup

```bash
cd frontend

npm install

npm run dev
```

Default frontend

```text
http://localhost:3000
```

Backend

```text
http://localhost:8080
```

---

# 👤 User Roles

## Student

* Browse Learning Materials
* Search Documents
* Download Files
* View Categories

---

## Lecturer

* Upload Materials
* Update Materials
* Delete Own Materials
* Manage Learning Resources

---

## Administrator

* Manage Users
* Manage Categories
* Manage Documents
* Manage Permissions
* View Dashboard

---

# 📄 Supported File Types

* PDF
* DOCX (Optional)
* PPTX (Optional)

---

# 🔍 Search Features

The application integrates Elasticsearch to provide fast and intelligent search capabilities.

### Keyword Search

Search documents using titles, descriptions, categories, or document content.

### Full-text Search

Retrieve relevant documents based on indexed PDF content.

### Metadata Search

Search by:

* Author
* Subject
* Category
* Upload Date
* File Name

### Search Result Highlighting

Matching keywords are highlighted within search results for better user experience.

---

# 📚 PDF Processing Workflow

```text
Upload PDF
      │
      ▼
Validate File
      │
      ▼
Extract Text (PDFBox / Tika)
      │
      ▼
Generate Metadata
      │
      ▼
Index into Elasticsearch
      │
      ▼
Store File
      │
      ▼
Search Ready
```

---

# 🗄 Database Modules

* Users
* Roles
* Materials
* Categories
* Subjects
* Downloads
* Audit Logs

---

# 🌐 REST API

## Authentication

```http
POST /api/auth/register

POST /api/auth/login

POST /api/auth/refresh-token
```

---

## Users

```http
GET /api/users

GET /api/users/{id}

PUT /api/users/{id}
```

---

## Materials

```http
GET /api/materials

POST /api/materials

PUT /api/materials/{id}

DELETE /api/materials/{id}
```

---

## Search

```http
GET /api/search

GET /api/search/keyword

GET /api/search/category
```

---

## Categories

```http
GET /api/categories

POST /api/categories

PUT /api/categories/{id}

DELETE /api/categories/{id}
```

---

# 🔒 Security

* Spring Security
* JWT Authentication
* BCrypt Password Encryption
* CORS Configuration
* Role-Based Access Control
* Protected REST APIs

---

# 📈 Future Enhancements

* AI-powered Semantic Search
* Document Recommendation System
* OCR for Scanned PDFs
* Document Version Control
* Bookmark & Favorites
* Learning Progress Tracking
* Comment & Rating System
* Email Notifications
* Dark Mode
* Mobile Responsive UI

---

# 👥 Contributors

| Name                 | Role                 |
| -------------------- | -------------------- |
| Nguyễn Hữu Bảo Khánh | Full Stack Developer |

---

# 📄 License


---

# 🙏 Acknowledgements

Special thanks to:

* Spring Boot
* React
* Elasticsearch
* Apache PDFBox
* Apache Tika
* MySQL
* Vite
* React Bootstrap
* FPT University

---

<p align="center">
Built with ❤️ using Spring Boot, React, MySQL & Elasticsearch
</p>
