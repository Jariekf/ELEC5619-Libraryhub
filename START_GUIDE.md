# LibraryHub - Quick Start Guide

## Prerequisites

Before starting the application, ensure you have:

-  **Java 17+** installed
-  **Node.js 16+** and npm installed
-  **MySQL** running on port 3306
-  **Maven** (included via mvnw wrapper)

## Database Setup

1. Start MySQL server:
   ```bash
   # Mac
   brew services start mysql
   
   # Linux
   sudo systemctl start mysql
   
   # Windows
   net start MySQL80
   ```

2. Create database:
   ```sql
   CREATE DATABASE libraryhub;
   ```

3. Configure database credentials in `libraryhub/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/libraryhub
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

## Quick Start (Automated)

### Using Start Script

1. Make scripts executable:
   ```bash
   chmod +x start.sh stop.sh
   ```

2. Start the application:
   ```bash
   ./start.sh
   ```

3. Access the application:
    - **Frontend**: http://localhost:5173
    - **Backend API**: http://localhost:8080

4. Stop the application:
   ```bash
   ./stop.sh
   ```

## Manual Start

### Option 1: Terminal Commands

**Terminal 1 - Backend:**
```bash
cd libraryhub
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd libraryhub-frontend
npm install
npm run dev
```

### Option 2: Using IntelliJ IDEA

**Backend:**
1. Open `libraryhub` folder in IntelliJ
2. Find `LibraryhubApplication.java`
3. Click green play button ▶️ next to `main` method
4. Wait for "Started LibraryhubApplication" message

**Frontend:**
1. Open Terminal in IntelliJ
2. Navigate to frontend folder:
   ```bash
   cd libraryhub-frontend
   npm install
   npm run dev
   ```

## Default Accounts

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: Full admin panel at `/admin`

### Customer Account
- **Username**: `customer`
- **Password**: `customer123`
- **Access**: Browse books, make reservations

## Application URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | Main application UI |
| Backend API | http://localhost:8080 | REST API endpoints |
| Admin Panel | http://localhost:5173/admin | Admin dashboard |
| API Docs | http://localhost:8080/swagger-ui.html | API documentation (if enabled) |
