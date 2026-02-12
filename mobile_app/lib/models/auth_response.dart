import 'user_model.dart';

class AuthResponse {
  final String token;
  final String username;
  final String role;

  AuthResponse({
    required this.token,
    required this.username,
    required this.role,
  });

  // Create AuthResponse from JSON
  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] as String,
      username: json['username'] as String,
      role: json['role'] as String,
    );
  }

  // Convert AuthResponse to JSON
  Map<String, dynamic> toJson() {
    return {
      'token': token,
      'username': username,
      'role': role,
    };
  }

  // Convert to User model
  User toUser() {
    return User(
      id: '', // ID will be set later if needed
      username: username,
      role: role,
    );
  }
}
