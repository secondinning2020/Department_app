class Employee {
  final String? id;
  final String name;
  final String email;
  final String position;
  final double salary;
  final String departmentId;
  final String? departmentName;

  Employee({
    this.id,
    required this.name,
    required this.email,
    required this.position,
    required this.salary,
    required this.departmentId,
    this.departmentName,
  });

  // Create Employee from JSON
  factory Employee.fromJson(Map<String, dynamic> json) {
    return Employee(
      id: json['id'] as String?,
      name: json['name'] as String,
      email: json['email'] as String,
      position: json['position'] as String,
      salary: (json['salary'] as num).toDouble(),
      departmentId: json['departmentId'] as String,
      departmentName: json['departmentName'] as String?,
    );
  }

  // Convert Employee to JSON
  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'name': name,
      'email': email,
      'position': position,
      'salary': salary,
      'departmentId': departmentId,
      if (departmentName != null) 'departmentName': departmentName,
    };
  }

  // Create a copy with modified fields
  Employee copyWith({
    String? id,
    String? name,
    String? email,
    String? position,
    double? salary,
    String? departmentId,
    String? departmentName,
  }) {
    return Employee(
      id: id ?? this.id,
      name: name ?? this.name,
      email: email ?? this.email,
      position: position ?? this.position,
      salary: salary ?? this.salary,
      departmentId: departmentId ?? this.departmentId,
      departmentName: departmentName ?? this.departmentName,
    );
  }
}
