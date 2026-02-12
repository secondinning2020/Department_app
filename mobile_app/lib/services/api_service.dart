import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import 'storage_service.dart';

class ApiService {
  final StorageService _storageService = StorageService();

  // Get headers with JWT token
  Future<Map<String, String>> _getHeaders() async {
    final token = await _storageService.getToken();
    
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  // GET request
  Future<http.Response> get(String endpoint) async {
    final url = Uri.parse('${ApiConfig.baseUrl}$endpoint');
    final headers = await _getHeaders();
    
    try {
      final response = await http.get(url, headers: headers)
          .timeout(ApiConfig.connectionTimeout);
      return response;
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  // POST request
  Future<http.Response> post(String endpoint, Map<String, dynamic> body) async {
    final url = Uri.parse('${ApiConfig.baseUrl}$endpoint');
    final headers = await _getHeaders();
    
    try {
      final response = await http.post(
        url,
        headers: headers,
        body: jsonEncode(body),
      ).timeout(ApiConfig.connectionTimeout);
      return response;
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  // PUT request
  Future<http.Response> put(String endpoint, Map<String, dynamic> body) async {
    final url = Uri.parse('${ApiConfig.baseUrl}$endpoint');
    final headers = await _getHeaders();
    
    try {
      final response = await http.put(
        url,
        headers: headers,
        body: jsonEncode(body),
      ).timeout(ApiConfig.connectionTimeout);
      return response;
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  // DELETE request
  Future<http.Response> delete(String endpoint) async {
    final url = Uri.parse('${ApiConfig.baseUrl}$endpoint');
    final headers = await _getHeaders();
    
    try {
      final response = await http.delete(url, headers: headers)
          .timeout(ApiConfig.connectionTimeout);
      return response;
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  // Handle API response
  dynamic handleResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) {
        return null;
      }
      return jsonDecode(response.body);
    } else if (response.statusCode == 401) {
      throw Exception('Unauthorized. Please login again.');
    } else if (response.statusCode == 403) {
      throw Exception('Forbidden. You don\'t have permission.');
    } else if (response.statusCode == 404) {
      throw Exception('Resource not found.');
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'An error occurred');
    }
  }
}
