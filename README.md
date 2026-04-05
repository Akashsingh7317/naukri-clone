# NaukriHub - Naukri.com Clone

## Tech Stack
- **Backend**: Java 17 + Spring Boot 3.2
- **Frontend**: HTML5, CSS3, JavaScript (Thymeleaf templates)
- **Database**: MySQL 8+
- **Security**: Spring Security (BCrypt)
- **ORM**: Spring Data JPA / Hibernate

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8+

### Step 1: Configure Database
Edit `src/main/resources/application.properties`:
```
spring.datasource.username=YOUR_MYSQL_USER
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 2: Create Database
MySQL will auto-create the `naukri_clone` database on first run.

### Step 3: Run
```bash
mvn spring-boot:run
```

### Step 4: Open Browser
Go to: http://localhost:8080

## Features
- 🔍 Job Search with filters (keyword, location, category)
- 👤 Job Seeker Registration & Login
- 🏢 Employer Registration & Login
- 📋 Post, Edit, Delete Jobs (Employer)
- 📄 Apply for Jobs with Cover Letter
- 📊 Application Status Tracking (Applied/Shortlisted/Interview/Selected/Rejected)
- 👤 Profile Management
- 🔒 Spring Security authentication
- 📱 Responsive Design

## Default Roles
- **JOB_SEEKER** - Browse & apply for jobs
- **EMPLOYER** - Post & manage jobs, view applicants
