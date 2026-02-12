import '../config/api_config.dart';
import '../models/department_model.dart';
import 'api_service.dart';

class DepartmentService {
  final ApiService _apiService = ApiService();

  // Get all departments
  Future<List<Department>> getAllDepartments() async {
    try {
      final response = await _apiService.get(ApiConfig.departmentsEndpoint);
      final data = _apiService.handleResponse(response) as List;
      
      return data.map((json) => Department.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to load departments: $e');
    }
  }

  // Get department by ID
  Future<Department> getDepartmentById(String id) async {
    try {
      final response = await _apiService.get('${ApiConfig.departmentsEndpoint}/$id');
      final data = _apiService.handleResponse(response);
      
      return Department.fromJson(data);
    } catch (e) {
      throw Exception('Failed to load department: $e');
    }
  }

  // Create department
  Future<Department> createDepartment(Department department) async {
    try {
      final response = await _apiService.post(
        ApiConfig.departmentsEndpoint,
        department.toJson(),
      );
      final data = _apiService.handleResponse(response);
      
      return Department.fromJson(data);
    } catch (e) {
      throw Exception('Failed to create department: $e');
    }
  }

  // Update department
  Future<Department> updateDepartment(String id, Department department) async {
    try {
      final response = await _apiService.put(
        '${ApiConfig.departmentsEndpoint}/$id',
        department.toJson(),
      );
      final data = _apiService.handleResponse(response);
      
      return Department.fromJson(data);
    } catch (e) {
      throw Exception('Failed to update department: $e');
    }
  }

  // Delete department
  Future<void> deleteDepartment(String id) async {
    try {
      await _apiService.delete('${ApiConfig.departmentsEndpoint}/$id');
    } catch (e) {
      throw Exception('Failed to delete department: $e');
    }
  }
}
