import '../config/api_config.dart';
import '../models/auth_response.dart';
import '../models/user_model.dart';
import 'api_service.dart';
import 'storage_service.dart';

class AuthService {
  final ApiService _apiService = ApiService();
  final StorageService _storageService = StorageService();

  // Login
  Future<AuthResponse> login(String username, String password) async {
    try {
      final response = await _apiService.post(
        ApiConfig.loginEndpoint,
        {
          'username': username,
          'password': password,
        },
      );

      final data = _apiService.handleResponse(response);
      final authResponse = AuthResponse.fromJson(data);

      // Save token and user info
      await _storageService.saveToken(authResponse.token);
      await _storageService.saveUsername(authResponse.username);
      await _storageService.saveRole(authResponse.role);

      return authResponse;
    } catch (e) {
      throw Exception('Login failed: $e');
    }
  }

  // Logout
  Future<void> logout() async {
    await _storageService.clearAll();
  }

  // Get current user
  Future<User?> getCurrentUser() async {
    final username = await _storageService.getUsername();
    final role = await _storageService.getRole();

    if (username != null && role != null) {
      return User(
        id: '',
        username: username,
        role: role,
      );
    }

    return null;
  }

  // Check if user is logged in
  Future<bool> isLoggedIn() async {
    return await _storageService.isLoggedIn();
  }

  // Get user role
  Future<String?> getUserRole() async {
    return await _storageService.getRole();
  }

  // Check if user has role
  Future<bool> hasRole(String role) async {
    final userRole = await getUserRole();
    return userRole == role;
  }

  // Check if user is admin or HR
  Future<bool> canManage() async {
    final role = await getUserRole();
    return role == 'ADMIN' || role == 'HR';
  }
}
