import 'package:flutter/material.dart';

class AppConstants {
  // App Info
  static const String appName = 'Department Manager';
  static const String appVersion = '1.0.0';
  
  // Storage Keys
  static const String tokenKey = 'jwt_token';
  static const String userKey = 'current_user';
  static const String usernameKey = 'username';
  static const String roleKey = 'user_role';
  
  // Colors
  static const Color primaryColor = Color(0xFF1976D2);
  static const Color secondaryColor = Color(0xFF424242);
  static const Color accentColor = Color(0xFFFF9800);
  static const Color errorColor = Color(0xFFD32F2F);
  static const Color successColor = Color(0xFF388E3C);
  
  // Text Styles
  static const TextStyle headingStyle = TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
    color: Colors.black87,
  );
  
  static const TextStyle subheadingStyle = TextStyle(
    fontSize: 18,
    fontWeight: FontWeight.w600,
    color: Colors.black87,
  );
  
  static const TextStyle bodyStyle = TextStyle(
    fontSize: 14,
    color: Colors.black87,
  );
  
  // Dimensions
  static const double defaultPadding = 16.0;
  static const double smallPadding = 8.0;
  static const double largePadding = 24.0;
  
  static const double borderRadius = 8.0;
  static const double cardElevation = 2.0;
  
  // User Roles
  static const String roleAdmin = 'ADMIN';
  static const String roleHR = 'HR';
  static const String roleEmployee = 'EMPLOYEE';
}
