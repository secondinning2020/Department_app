# Department Management System

Enterprise Department and Employee Management System - Production-Ready Spring Boot Application

## 🚀 Features

- **Role-Based Access Control (RBAC)**: ADMIN, HR, and EMPLOYEE roles
- **JWT Authentication**: Secure token-based authentication
- **RESTful API**: Versioned API endpoints (/api/v1)
- **PDF Reports**: JasperReports integration for department reports
- **Custom Employee ID**: Auto-generated sequential IDs (EMP001, EMP002, etc.)
- **H2 Database**: In-memory database for development
- **Production Ready**: PostgreSQL/MySQL support for production
- **API Documentation**: Swagger/OpenAPI 3 integration
- **Global Exception Handling**: Standardized error responses
- **Transaction Management**: Proper transaction boundaries
- **Logging**: SLF4J logging throughout the application

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Security** with JWT
- **Spring Data JPA**
- **H2 Database** (Development)
- **PostgreSQL/MySQL** (Production)
- **JasperReports 6.21.0**
- **Swagger/OpenAPI 3**
- **Lombok**
- **MapStruct**
- **Maven**

## 🏗️ Project Structure

```
backend/
├── src/main/java/com/enterprise/department/
│   ├── controller/          # REST endpoints
│   ├── service/             # Business logic
│   ├── repository/          # JPA repositories
│   ├── entity/              # JPA entities
│   ├── dto/                 # Data Transfer Objects
│   ├── mapper/              # Entity ↔ DTO mappers
│   ├── security/            # JWT & Security
│   ├── config/              # Configuration classes
│   ├── exception/           # Exception handling
│   └── util/                # Utility classes
├── src/main/resources/
│   ├── application.yml      # Base configuration
│   ├── application-dev.yml  # Development profile
│   ├── application-prod.yml # Production profile
│   └── reports/             # JasperReports templates
└── pom.xml
```

## 🚦 Getting Started

### 1. Clone the Repository

```bash
cd c:\Users\Asus\Downloads\ASPDepartment\backend
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8084**

### 4. Access H2 Console

- URL: http://localhost:8084/h2-console
- JDBC URL: `jdbc:h2:mem:departmentdb`
- Username: `sa`
- Password: (leave blank)

### 5. Access Swagger UI

- URL: http://localhost:8084/swagger-ui.html
- Interactive API documentation with JWT authentication support

## 🔐 Default Users

The application comes with pre-configured users:

| Username | Password | Role     |
|----------|----------|----------|
| admin    | admin123 | ADMIN    |
| hr       | hr123    | HR       |
| employee | emp123   | EMPLOYEE |

## 📡 API Endpoints

### Authentication

- `POST /api/v1/auth/login` - User login (Public)
- `POST /api/v1/auth/register` - Register user (ADMIN only)

### Departments

- `GET /api/v1/departments` - Get all departments (Authenticated)
- `GET /api/v1/departments/{id}` - Get department by ID (Authenticated)
- `POST /api/v1/departments` - Create department (ADMIN/HR)
- `PUT /api/v1/departments/{id}` - Update department (ADMIN/HR)
- `DELETE /api/v1/departments/{id}` - Delete department (ADMIN)

### Employees

- `GET /api/v1/employees` - Get all employees (Authenticated)
- `GET /api/v1/departments/{deptId}/employees` - Get employees by department (Authenticated)
- `GET /api/v1/employees/{empId}` - Get employee by ID (Authenticated)
- `POST /api/v1/departments/{deptId}/employees` - Create employee (ADMIN/HR)
- `PUT /api/v1/departments/{deptId}/employees/{empId}` - Update employee (ADMIN/HR)
- `DELETE /api/v1/departments/{deptId}/employees/{empId}` - Delete employee (ADMIN)
- `GET /api/v1/employees/grouped` - Group employees by department (Authenticated)
- `GET /api/v1/employees/salary-summary` - Calculate salary by department (ADMIN/HR)

### Reports

- `GET /api/v1/reports/departments` - Generate department PDF report (ADMIN/HR)

## 🧪 Testing with Swagger

1. Navigate to http://localhost:8084/swagger-ui.html
2. Click on **Authorize** button
3. Login using `/api/v1/auth/login` endpoint
4. Copy the JWT token from the response
5. Click **Authorize** again and enter: `Bearer <your-token>`
6. Now you can test all protected endpoints

## 🔧 Configuration

### Development Profile (application-dev.yml)

- H2 in-memory database
- H2 console enabled
- Debug logging
- Swagger UI enabled

### Production Profile (application-prod.yml)

- PostgreSQL/MySQL configuration
- Production logging levels
- Swagger disabled (or secured)
- Environment variable based configuration

### Environment Variables for Production

```bash
DB_URL=jdbc:postgresql://localhost:5432/departmentdb
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_secure_secret_key
```

## 📊 Sample Data

The application automatically seeds sample data on startup:

- **3 Departments**: IT, HR, Finance
- **8 Employees**: Distributed across departments
- **3 Users**: admin, hr, employee

## 🏭 Production Deployment

### Using PostgreSQL

1. Update `application-prod.yml` with PostgreSQL configuration
2. Set environment variables:
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export DB_URL=jdbc:postgresql://localhost:5432/departmentdb
   export DB_USERNAME=postgres
   export DB_PASSWORD=your_password
   export JWT_SECRET=your_secure_secret_key
   ```
3. Build and run:
   ```bash
   mvn clean package
   java -jar target/department-management-1.0.0.jar
   ```

### Using MySQL

1. Uncomment MySQL configuration in `application-prod.yml`
2. Comment out PostgreSQL configuration
3. Set environment variables accordingly

## 📝 Logging

Logs are configured with SLF4J and include:

- Request/Response logging
- Business operation logging
- Error logging with stack traces
- Security event logging

## 🔒 Security Features

- JWT-based authentication
- BCrypt password encoding
- Role-based access control (@PreAuthorize)
- CORS configuration
- Stateless session management
- Secure password storage

## 🎯 Best Practices Implemented

- ✅ Layered architecture
- ✅ DTO pattern for API responses
- ✅ Global exception handling
- ✅ Transaction management
- ✅ Input validation
- ✅ Logging throughout
- ✅ API versioning
- ✅ Swagger documentation
- ✅ Separation of concerns
- ✅ Custom business key generation

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JasperReports](https://community.jaspersoft.com/)
- [Swagger/OpenAPI](https://swagger.io/)

## 🤝 Support

For issues or questions, please contact the development team.

## 📄 License

Apache 2.0
