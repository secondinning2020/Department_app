class Department {
  final String? id;
  final String name;
  final String location;
  final int? employeeCount;

  Department({
    this.id,
    required this.name,
    required this.location,
    this.employeeCount,
  });

  // Create Department from JSON
  factory Department.fromJson(Map<String, dynamic> json) {
    return Department(
      id: json['id'] as String?,
      name: json['name'] as String,
      location: json['location'] as String,
      employeeCount: json['employeeCount'] as int?,
    );
  }

  // Convert Department to JSON
  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'name': name,
      'location': location,
      if (employeeCount != null) 'employeeCount': employeeCount,
    };
  }

  // Create a copy with modified fields
  Department copyWith({
    String? id,
    String? name,
    String? location,
    int? employeeCount,
  }) {
    return Department(
      id: id ?? this.id,
      name: name ?? this.name,
      location: location ?? this.location,
      employeeCount: employeeCount ?? this.employeeCount,
    );
  }
}
