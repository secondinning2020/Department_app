import '../config/api_config.dart';
import '../models/employee_model.dart';
import 'api_service.dart';

class EmployeeService {
  final ApiService _apiService = ApiService();

  // Get all employees
  Future<List<Employee>> getAllEmployees() async {
    try {
      final response = await _apiService.get(ApiConfig.employeesEndpoint);
      final data = _apiService.handleResponse(response) as List;
      
      return data.map((json) => Employee.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to load employees: $e');
    }
  }

  // Get employees by department
  Future<List<Employee>> getEmployeesByDepartment(String departmentId) async {
    try {
      final response = await _apiService.get(
        '${ApiConfig.departmentsEndpoint}/$departmentId/employees',
      );
      final data = _apiService.handleResponse(response) as List;
      
      return data.map((json) => Employee.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to load employees: $e');
    }
  }

  // Get employee by ID
  Future<Employee> getEmployeeById(String departmentId, String employeeId) async {
    try {
      final response = await _apiService.get(
        '${ApiConfig.departmentsEndpoint}/$departmentId/employees/$employeeId',
      );
      final data = _apiService.handleResponse(response);
      
      return Employee.fromJson(data);
    } catch (e) {
      throw Exception('Failed to load employee: $e');
    }
  }

  // Create employee
  Future<Employee> createEmployee(String departmentId, Employee employee) async {
    try {
      final response = await _apiService.post(
        '${ApiConfig.departmentsEndpoint}/$departmentId/employees',
        employee.toJson(),
      );
      final data = _apiService.handleResponse(response);
      
      return Employee.fromJson(data);
    } catch (e) {
      throw Exception('Failed to create employee: $e');
    }
  }

  // Update employee
  Future<Employee> updateEmployee(
    String departmentId,
    String employeeId,
    Employee employee,
  ) async {
    try {
      final response = await _apiService.put(
        '${ApiConfig.departmentsEndpoint}/$departmentId/employees/$employeeId',
        employee.toJson(),
      );
      final data = _apiService.handleResponse(response);
      
      return Employee.fromJson(data);
    } catch (e) {
      throw Exception('Failed to update employee: $e');
    }
  }

  // Delete employee
  Future<void> deleteEmployee(String departmentId, String employeeId) async {
    try {
      await _apiService.delete(
        '${ApiConfig.departmentsEndpoint}/$departmentId/employees/$employeeId',
      );
    } catch (e) {
      throw Exception('Failed to delete employee: $e');
    }
  }
}
