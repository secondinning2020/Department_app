import 'package:flutter/foundation.dart';
import '../models/employee_model.dart';
import '../services/employee_service.dart';

class EmployeeController with ChangeNotifier {
  final EmployeeService _employeeService = EmployeeService();
  
  List<Employee> _employees = [];
  Employee? _selectedEmployee;
  bool _isLoading = false;
  String? _errorMessage;
  String? _filterDepartmentId;

  // Getters
  List<Employee> get employees {
    if (_filterDepartmentId != null) {
      return _employees.where((e) => e.departmentId == _filterDepartmentId).toList();
    }
    return _employees;
  }
  
  Employee? get selectedEmployee => _selectedEmployee;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  String? get filterDepartmentId => _filterDepartmentId;

  // Load all employees
  Future<void> loadEmployees() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _employees = await _employeeService.getAllEmployees();
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Load employees by department
  Future<void> loadEmployeesByDepartment(String departmentId) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _employees = await _employeeService.getEmployeesByDepartment(departmentId);
      _filterDepartmentId = departmentId;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Load employee by ID
  Future<void> loadEmployeeById(String departmentId, String employeeId) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _selectedEmployee = await _employeeService.getEmployeeById(departmentId, employeeId);
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Create employee
  Future<bool> createEmployee(String departmentId, Employee employee) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final newEmployee = await _employeeService.createEmployee(departmentId, employee);
      _employees.add(newEmployee);
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  // Update employee
  Future<bool> updateEmployee(
    String departmentId,
    String employeeId,
    Employee employee,
  ) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final updatedEmployee = await _employeeService.updateEmployee(
        departmentId,
        employeeId,
        employee,
      );
      final index = _employees.indexWhere((e) => e.id == employeeId);
      if (index != -1) {
        _employees[index] = updatedEmployee;
      }
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  // Delete employee
  Future<bool> deleteEmployee(String departmentId, String employeeId) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      await _employeeService.deleteEmployee(departmentId, employeeId);
      _employees.removeWhere((e) => e.id == employeeId);
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  // Set filter by department
  void setDepartmentFilter(String? departmentId) {
    _filterDepartmentId = departmentId;
    notifyListeners();
  }

  // Clear filter
  void clearFilter() {
    _filterDepartmentId = null;
    notifyListeners();
  }

  // Clear error message
  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  // Set selected employee
  void setSelectedEmployee(Employee? employee) {
    _selectedEmployee = employee;
    notifyListeners();
  }
}
