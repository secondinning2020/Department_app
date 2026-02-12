import 'package:flutter/foundation.dart';
import '../models/department_model.dart';
import '../services/department_service.dart';

class DepartmentController with ChangeNotifier {
  final DepartmentService _departmentService = DepartmentService();
  
  List<Department> _departments = [];
  Department? _selectedDepartment;
  bool _isLoading = false;
  String? _errorMessage;

  // Getters
  List<Department> get departments => _departments;
  Department? get selectedDepartment => _selectedDepartment;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  // Load all departments
  Future<void> loadDepartments() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _departments = await _departmentService.getAllDepartments();
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Load department by ID
  Future<void> loadDepartmentById(String id) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _selectedDepartment = await _departmentService.getDepartmentById(id);
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Create department
  Future<bool> createDepartment(Department department) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final newDepartment = await _departmentService.createDepartment(department);
      _departments.add(newDepartment);
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

  // Update department
  Future<bool> updateDepartment(String id, Department department) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final updatedDepartment = await _departmentService.updateDepartment(id, department);
      final index = _departments.indexWhere((d) => d.id == id);
      if (index != -1) {
        _departments[index] = updatedDepartment;
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

  // Delete department
  Future<bool> deleteDepartment(String id) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      await _departmentService.deleteDepartment(id);
      _departments.removeWhere((d) => d.id == id);
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

  // Clear error message
  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  // Set selected department
  void setSelectedDepartment(Department? department) {
    _selectedDepartment = department;
    notifyListeners();
  }
}
