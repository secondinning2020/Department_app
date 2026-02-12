# Postman Collection Guide - Department Management System API

## 📦 What's Included

I've created a complete Postman collection for testing the Department Management System API with:

- **20+ API requests** organized by category
- **Automated test scripts** for each endpoint
- **Environment variables** for easy configuration
- **JWT authentication** setup
- **Sample request bodies** for all POST/PUT operations

## 📁 Files Created

1. **Department_Management_API.postman_collection.json** - Main collection file
2. **Department_Management_Local.postman_environment.json** - Environment variables

## 🚀 Getting Started

### Step 1: Import Collection into Postman

1. Open Postman
2. Click **Import** button (top left)
3. Select **File** tab
4. Choose both JSON files:
   - `Department_Management_API.postman_collection.json`
   - `Department_Management_Local.postman_environment.json`
5. Click **Import**

### Step 2: Select Environment

1. In Postman, look for the environment dropdown (top right)
2. Select **Department Management - Local**
3. Verify the base URL is set to `http://localhost:8084`

### Step 3: Ensure Backend is Running

Make sure your Spring Boot application is running on port 8084:
```bash
cd c:\Users\Asus\Downloads\ASPDepartment\backend
mvn spring-boot:run
```

## 📋 Collection Structure

### 1. Authentication (4 requests)
- **Login - Admin**: Get JWT token with ADMIN role
- **Login - HR**: Get JWT token with HR role  
- **Login - Employee**: Get JWT token with EMPLOYEE role
- **Register User**: Create new user (ADMIN only)

### 2. Departments (5 requests)
- **Get All Departments**: List all departments
- **Get Department by ID**: Get specific department with employees
- **Create Department**: Add new department (ADMIN/HR)
- **Update Department**: Modify department (ADMIN/HR)
- **Delete Department**: Remove department (ADMIN)

### 3. Employees (8 requests)
- **Get All Employees**: List all employees
- **Get Employee by ID**: Get specific employee details
- **Get Employees by Department**: Filter by department
- **Create Employee**: Add new employee (ADMIN/HR)
- **Update Employee**: Modify employee (ADMIN/HR)
- **Delete Employee**: Remove employee (ADMIN)
- **Group Employees by Department**: Get grouped data
- **Get Salary Summary**: Calculate totals by department (ADMIN/HR)

### 4. Reports (1 request)
- **Generate Department Report PDF**: Download PDF report (ADMIN/HR)

### 5. Health Check (1 request)
- **Actuator Health**: Verify application status

## 🔐 Authentication Flow

### Quick Start - Get JWT Token

1. Open the **Authentication** folder
2. Run **Login - Admin** request
3. The JWT token is **automatically saved** to the `jwt_token` variable
4. All subsequent requests will use this token automatically

### Default Credentials

| Username | Password  | Role     |
|----------|-----------|----------|
| admin    | admin123  | ADMIN    |
| hr       | hr123     | HR       |
| employee | emp123    | EMPLOYEE |

## ✅ Automated Tests

Each request includes automated tests that verify:

### Authentication Tests
- ✅ Status code is 200
- ✅ Response contains JWT token
- ✅ User role matches expected value
- ✅ Token is saved to environment variable

### CRUD Operation Tests
- ✅ Status codes (200, 204, etc.)
- ✅ Response data structure
- ✅ Required fields present
- ✅ Data validation
- ✅ Auto-save IDs for subsequent requests

### Example Test Results
After running a request, check the **Test Results** tab to see:
- ✅ Status code is 200 (PASS)
- ✅ Response has token (PASS)
- ✅ User role is ADMIN (PASS)

## 🎯 Common Testing Scenarios

### Scenario 1: Complete CRUD Flow for Departments

1. **Login as Admin**
   - Run: `Authentication > Login - Admin`
   - Verify: Token saved automatically

2. **View All Departments**
   - Run: `Departments > Get All Departments`
   - Verify: Returns array of departments

3. **Create New Department**
   - Run: `Departments > Create Department`
   - Verify: Department created with ID
   - Note: Department ID saved automatically

4. **Update Department**
   - Run: `Departments > Update Department`
   - Verify: Location updated to "Building E"

5. **Delete Department**
   - Run: `Departments > Delete Department`
   - Verify: Status 204 (No Content)

### Scenario 2: Employee Management

1. **Login as HR**
   - Run: `Authentication > Login - HR`

2. **Get Department ID**
   - Run: `Departments > Get All Departments`
   - Note: First department ID saved automatically

3. **View Department Employees**
   - Run: `Employees > Get Employees by Department`
   - Verify: All employees belong to same department

4. **Add New Employee**
   - Run: `Employees > Create Employee`
   - Verify: Employee ID in format EMP001, EMP002, etc.

5. **Update Employee Salary**
   - Run: `Employees > Update Employee`
   - Modify salary in request body
   - Verify: Salary updated

### Scenario 3: Reporting

1. **Login as Admin or HR**
   - Run: `Authentication > Login - Admin`

2. **Generate PDF Report**
   - Run: `Reports > Generate Department Report PDF`
   - Verify: Content-Type is application/pdf
   - Click **Save Response > Save to a file**
   - Save as `department_report.pdf`

### Scenario 4: Role-Based Access Control Testing

1. **Login as Employee**
   - Run: `Authentication > Login - Employee`

2. **Try to Create Department** (Should Fail)
   - Run: `Departments > Create Department`
   - Expected: 403 Forbidden

3. **View Departments** (Should Succeed)
   - Run: `Departments > Get All Departments`
   - Expected: 200 OK

## 🔄 Running Collection with Collection Runner

### Run All Tests Automatically

1. Click on the collection name
2. Click **Run** button
3. Select all folders or specific folders
4. Click **Run Department Management System API**
5. View test results for all requests

### Best Order for Collection Runner

1. Authentication
2. Departments
3. Employees  
4. Reports
5. Health Check

## 📊 Environment Variables

The collection uses these variables (auto-managed):

| Variable       | Description                    | Auto-Set |
|----------------|--------------------------------|----------|
| base_url       | API base URL                   | ✅       |
| jwt_token      | JWT authentication token       | ✅       |
| department_id  | Last created/fetched dept ID   | ✅       |
| employee_id    | Last created/fetched emp ID    | ✅       |

You can view/edit these in: **Environments > Department Management - Local**

## 🎨 Customizing Requests

### Modify Request Body

1. Select any POST/PUT request
2. Go to **Body** tab
3. Edit the JSON data
4. Click **Send**

Example - Create Employee with different data:
```json
{
  "name": "Your Name",
  "email": "your.email@example.com",
  "position": "Your Position",
  "salary": 90000
}
```

### Test Different Scenarios

**Valid Email Test:**
```json
{
  "name": "Test User",
  "email": "test@example.com",
  "position": "Tester",
  "salary": 50000
}
```

**Duplicate Email Test** (Should fail):
```json
{
  "name": "Another User",
  "email": "john.doe@example.com",
  "position": "Developer",
  "salary": 75000
}
```

**Invalid Salary Test** (Should fail):
```json
{
  "name": "Invalid User",
  "email": "invalid@example.com",
  "position": "Developer",
  "salary": -1000
}
```

## 🐛 Troubleshooting

### Issue: "Could not get response"
**Solution**: Ensure backend is running on port 8084
```bash
curl http://localhost:8084/actuator/health
```

### Issue: "401 Unauthorized"
**Solution**: 
1. Run login request again
2. Verify JWT token is saved in environment
3. Check token hasn't expired (24 hour validity)

### Issue: "403 Forbidden"
**Solution**: 
- Verify you're logged in with correct role
- ADMIN: Full access
- HR: Can manage departments and employees
- EMPLOYEE: Read-only access

### Issue: "404 Not Found"
**Solution**:
- Verify the department_id or employee_id exists
- Run "Get All Departments" or "Get All Employees" first

## 📈 Advanced Features

### Pre-request Scripts

Some requests automatically:
- Extract IDs from responses
- Save to environment variables
- Set up data for next request

### Test Scripts

All requests include:
- Status code validation
- Response structure validation
- Data type checking
- Automatic variable extraction

### Chaining Requests

The collection is designed for request chaining:
1. Login → Token saved
2. Get Departments → Department ID saved
3. Create Employee → Uses saved Department ID
4. Update Employee → Uses saved Employee ID

## 🎯 Next Steps

1. **Import the collection** into Postman
2. **Run the health check** to verify backend
3. **Login as Admin** to get JWT token
4. **Explore all endpoints** systematically
5. **Run Collection Runner** to execute all tests

## 📝 Sample Test Workflow

```
1. Health Check → Verify backend is UP
2. Login - Admin → Get JWT token
3. Get All Departments → See existing data
4. Get All Employees → See existing employees
5. Create Department → Add "Marketing"
6. Create Employee → Add employee to Marketing
7. Get Employees by Department → Verify employee added
8. Get Salary Summary → See salary totals
9. Generate PDF Report → Download report
10. Delete Employee → Clean up
11. Delete Department → Clean up
```

## 🎉 Summary

You now have a complete Postman collection with:
- ✅ 20+ pre-configured requests
- ✅ Automated authentication
- ✅ Built-in test scripts
- ✅ Environment variables
- ✅ Sample data
- ✅ RBAC testing capabilities

Happy testing! 🚀
