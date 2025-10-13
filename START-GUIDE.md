#  LibraryHub Quick Start Guide

This guide will help you quickly set up and run the LibraryHub full-stack application (Spring Boot backend + React frontend).

---

##  Prerequisites

Before starting, ensure you have the following installed:

1. **Java 17+** - Required for Spring Boot backend
2. **Maven** - For building the backend (or use the included `mvnw`)
3. **Node.js 18+** - Required for React frontend
4. **MySQL** - Database server
5. **Git** - For cloning the repository

---

##  Database Setup

### 1. Start MySQL Server

Make sure your MySQL server is running.

### 2. Create Database and User

```sql
CREATE DATABASE librarydb;
CREATE USER 'libraryuser'@'localhost' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON librarydb.* TO 'libraryuser'@'localhost';
FLUSH PRIVILEGES;
```

---

##  Quick Start (Recommended)

### Method 1: Using Startup Script

```bash
# Grant execute permissions (first time only)
chmod +x start-app.sh stop-app.sh

# Start the application
./start-app.sh
```

The script will automatically:
- ✅ Check if ports are available
- ✅ Start the backend (port 8080)
- ✅ Start the frontend (port 5173)
- ✅ **Automatically open your browser to the login page**
- ✅ Display live logs

**To stop the application:**
```bash
# Press Ctrl+C to stop log display, then run:
./stop-app.sh
```

---

##  Manual Startup

### Backend (Spring Boot)

```bash
cd libraryhub

# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or using system Maven
mvn spring-boot:run
```

The backend will start on **http://localhost:8080**

### Frontend (React)

```bash
cd libraryhub-frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend will start on **http://localhost:5173**

---

##  Accessing the Application

### Frontend (React UI)
- **URL**: http://localhost:5173
- **Login Page**: http://localhost:5173/login
- **Home Page**: http://localhost:5173/

### Backend (Spring Boot + Thymeleaf)
- **URL**: http://localhost:8080
- **Books Page**: http://localhost:8080/books
- **AI Chatbot**: http://localhost:8080/chatbot
- **Health Check**: http://localhost:8080/actuator/health

---

##  Default Login Credentials

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`

### Customer Account
- You need to **register** a new customer account via the registration page
- Or use the backend registration: http://localhost:8080/auth/register

---

##  Features by User Role

###  Admin Features
- ✅ View all books
- ✅ Add new books
- ✅ Edit book details
- ✅ Delete books
- ✅ View all reservations
- ✅ Manage customers
- ✅ Delete reviews
- ✅ Access AI chatbot

###  Customer Features
- ✅ Browse available books
- ✅ Search for books
- ✅ Reserve available books
- ✅ Return borrowed books
- ✅ Join waiting lists for unavailable books
- ✅ Write and delete own reviews
- ✅ View book details and reviews
- ✅ Access AI chatbot

---

##  Troubleshooting

### Port Already in Use

If you see errors about ports 8080 or 5173 being in use:

```bash
# Kill process on port 8080 (backend)
lsof -ti:8080 | xargs kill -9

# Kill process on port 5173 (frontend)
lsof -ti:5173 | xargs kill -9
```

### Database Connection Error

Check your `libraryhub/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/librarydb
spring.datasource.username=libraryuser
spring.datasource.password=mypassword
```

Make sure these match your MySQL configuration.

### Frontend Build Errors

If you encounter build errors in the React frontend:

```bash
cd libraryhub-frontend

# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Restart dev server
npm run dev
```

### CORS Errors

If you see CORS errors in the browser console:
- Make sure both backend (8080) and frontend (5173) are running
- Check that `CorsConfig.java` allows `http://localhost:5173`
- Restart both servers after configuration changes

---

##  Monitoring Logs

### Using the Startup Script

When using `./start-app.sh`, logs are automatically displayed. You can also view them separately:

```bash
# View backend logs
tail -f backend.log

# View frontend logs
tail -f frontend.log
```

### Manual Monitoring

If you started services manually, logs will appear in the respective terminals.

---

##  Development Workflow

### Making Backend Changes

1. Edit Java files in `libraryhub/src/main/java/`
2. Stop the backend (Ctrl+C)
3. Restart: `./mvnw spring-boot:run`
4. Spring Boot DevTools may auto-reload for some changes

### Making Frontend Changes

1. Edit React/TypeScript files in `libraryhub-frontend/src/`
2. Vite will automatically hot-reload changes
3. No restart needed for most changes

---

##  Project Structure

```
ELEC5619_Practical02_Group_11/
├── libraryhub/                    # Spring Boot Backend
│   ├── src/main/java/            # Java source files
│   ├── src/main/resources/       # Application properties, templates
│   └── pom.xml                   # Maven dependencies
│
├── libraryhub-frontend/          # React Frontend
│   ├── src/                      # React components, services
│   ├── package.json              # NPM dependencies
│   └── vite.config.ts            # Vite configuration
│
├── start-app.sh                  # Startup script
├── stop-app.sh                   # Stop script
└── START-GUIDE.md                # This guide
```

---


**Happy coding! 🎉**
