import 'package:flutter/foundation.dart' show kIsWeb;

class ApiConfig {
  // Base URL - use localhost for web, 10.0.2.2 for Android emulator
  static String get baseUrl {
    if (kIsWeb) {
      return 'http://localhost:8084';
    } else {
      // For Android emulator
      return 'http://10.0.2.2:8084';
    }
  }

  // API Endpoints
  static const String apiVersion = '/api/v1';
  static const String loginEndpoint = '$apiVersion/auth/login';
  static const String departmentsEndpoint = '$apiVersion/departments';
  static const String employeesEndpoint = '$apiVersion/employees';

  // Timeout durations
  static const Duration connectionTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
}
