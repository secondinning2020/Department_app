import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../utils/constants.dart';

class StorageService {
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  // Save JWT token
  Future<void> saveToken(String token) async {
    await _secureStorage.write(key: AppConstants.tokenKey, value: token);
  }

  // Get JWT token
  Future<String?> getToken() async {
    return await _secureStorage.read(key: AppConstants.tokenKey);
  }

  // Delete JWT token
  Future<void> deleteToken() async {
    await _secureStorage.delete(key: AppConstants.tokenKey);
  }

  // Save username
  Future<void> saveUsername(String username) async {
    await _secureStorage.write(key: AppConstants.usernameKey, value: username);
  }

  // Get username
  Future<String?> getUsername() async {
    return await _secureStorage.read(key: AppConstants.usernameKey);
  }

  // Save user role
  Future<void> saveRole(String role) async {
    await _secureStorage.write(key: AppConstants.roleKey, value: role);
  }

  // Get user role
  Future<String?> getRole() async {
    return await _secureStorage.read(key: AppConstants.roleKey);
  }

  // Clear all stored data
  Future<void> clearAll() async {
    await _secureStorage.deleteAll();
  }

  // Check if user is logged in
  Future<bool> isLoggedIn() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }
}
