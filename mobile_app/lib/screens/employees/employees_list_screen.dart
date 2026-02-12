import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../controllers/auth_controller.dart';
import '../../controllers/employee_controller.dart';
import '../../controllers/department_controller.dart';
import '../../models/employee_model.dart';
import '../../utils/constants.dart';
import 'employee_form_screen.dart';

class EmployeesListScreen extends StatefulWidget {
  const EmployeesListScreen({super.key});

  @override
  State<EmployeesListScreen> createState() => _EmployeesListScreenState();
}

class _EmployeesListScreenState extends State<EmployeesListScreen> {
  String _searchQuery = '';
  String? _selectedDepartmentId;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final empController = Provider.of<EmployeeController>(context, listen: false);
    final deptController = Provider.of<DepartmentController>(context, listen: false);
    
    await Future.wait([
      empController.loadEmployees(),
      deptController.loadDepartments(),
    ]);
  }

  Future<void> _deleteEmployee(Employee employee) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Employee'),
        content: Text('Are you sure you want to delete "${employee.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: AppConstants.errorColor),
            child: const Text('Delete'),
          ),
        ],
      ),
    );

    if (confirmed == true && mounted) {
      final controller = Provider.of<EmployeeController>(context, listen: false);
      final success = await controller.deleteEmployee(employee.departmentId, employee.id!);
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(success ? 'Employee deleted' : controller.errorMessage ?? 'Failed to delete'),
            backgroundColor: success ? AppConstants.successColor : AppConstants.errorColor,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final currencyFormat = NumberFormat.currency(symbol: '\$', decimalDigits: 0);
    
    return Scaffold(
      appBar: AppBar(
        title: const Text('Employees'),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(120),
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: AppConstants.smallPadding),
                child: TextField(
                  decoration: InputDecoration(
                    hintText: 'Search employees...',
                    prefixIcon: const Icon(Icons.search),
                    filled: true,
                    fillColor: Colors.white,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      borderSide: BorderSide.none,
                    ),
                  ),
                  onChanged: (value) {
                    setState(() {
                      _searchQuery = value.toLowerCase();
                    });
                  },
                ),
              ),
              const SizedBox(height: 8),
              Consumer<DepartmentController>(
                builder: (context, deptController, child) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(horizontal: AppConstants.smallPadding),
                    child: DropdownButtonFormField<String>(
                      value: _selectedDepartmentId,
                      decoration: InputDecoration(
                        labelText: 'Filter by Department',
                        prefixIcon: const Icon(Icons.business),
                        filled: true,
                        fillColor: Colors.white,
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                          borderSide: BorderSide.none,
                        ),
                      ),
                      items: [
                        const DropdownMenuItem(
                          value: null,
                          child: Text('All Departments'),
                        ),
                        ...deptController.departments.map((dept) {
                          return DropdownMenuItem(
                            value: dept.id,
                            child: Text(dept.name),
                          );
                        }),
                      ],
                      onChanged: (value) {
                        setState(() {
                          _selectedDepartmentId = value;
                        });
                      },
                    ),
                  );
                },
              ),
              const SizedBox(height: 8),
            ],
          ),
        ),
      ),
      body: Consumer<EmployeeController>(
        builder: (context, empController, child) {
          if (empController.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (empController.errorMessage != null) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 64, color: AppConstants.errorColor),
                  const SizedBox(height: 16),
                  Text(empController.errorMessage!),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: _loadData,
                    child: const Text('Retry'),
                  ),
                ],
              ),
            );
          }

          var employees = empController.employees;
          
          // Apply department filter
          if (_selectedDepartmentId != null) {
            employees = employees.where((e) => e.departmentId == _selectedDepartmentId).toList();
          }
          
          // Apply search filter
          if (_searchQuery.isNotEmpty) {
            employees = employees.where((e) =>
                e.name.toLowerCase().contains(_searchQuery) ||
                e.email.toLowerCase().contains(_searchQuery) ||
                e.position.toLowerCase().contains(_searchQuery)).toList();
          }

          if (employees.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.people_outlined, size: 64, color: Colors.grey),
                  const SizedBox(height: 16),
                  Text(
                    _searchQuery.isEmpty && _selectedDepartmentId == null
                        ? 'No employees found'
                        : 'No matching employees',
                    style: const TextStyle(fontSize: 16, color: Colors.grey),
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: _loadData,
            child: ListView.builder(
              padding: const EdgeInsets.all(AppConstants.defaultPadding),
              itemCount: employees.length,
              itemBuilder: (context, index) {
                final employee = employees[index];
                return Card(
                  elevation: AppConstants.cardElevation,
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: AppConstants.accentColor.withOpacity(0.2),
                      child: Text(
                        employee.name.substring(0, 1).toUpperCase(),
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          color: AppConstants.accentColor,
                        ),
                      ),
                    ),
                    title: Text(
                      employee.name,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(height: 4),
                        Text(employee.position),
                        const SizedBox(height: 2),
                        Row(
                          children: [
                            const Icon(Icons.email, size: 12, color: Colors.grey),
                            const SizedBox(width: 4),
                            Expanded(child: Text(employee.email, style: const TextStyle(fontSize: 12))),
                          ],
                        ),
                        const SizedBox(height: 2),
                        Row(
                          children: [
                            const Icon(Icons.attach_money, size: 12, color: Colors.grey),
                            const SizedBox(width: 4),
                            Text(currencyFormat.format(employee.salary), style: const TextStyle(fontSize: 12)),
                          ],
                        ),
                      ],
                    ),
                    trailing: Consumer<AuthController>(
                      builder: (context, authController, child) {
                        return FutureBuilder<bool>(
                          future: authController.canManage(),
                          builder: (context, snapshot) {
                            if (snapshot.data == true) {
                              return PopupMenuButton(
                                itemBuilder: (context) => [
                                  const PopupMenuItem(
                                    value: 'edit',
                                    child: Row(
                                      children: [
                                        Icon(Icons.edit, size: 20),
                                        SizedBox(width: 8),
                                        Text('Edit'),
                                      ],
                                    ),
                                  ),
                                  const PopupMenuItem(
                                    value: 'delete',
                                    child: Row(
                                      children: [
                                        Icon(Icons.delete, size: 20, color: AppConstants.errorColor),
                                        SizedBox(width: 8),
                                        Text('Delete', style: TextStyle(color: AppConstants.errorColor)),
                                      ],
                                    ),
                                  ),
                                ],
                                onSelected: (value) {
                                  if (value == 'edit') {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (_) => EmployeeFormScreen(employee: employee),
                                      ),
                                    ).then((_) => _loadData());
                                  } else if (value == 'delete') {
                                    _deleteEmployee(employee);
                                  }
                                },
                              );
                            }
                            return const SizedBox.shrink();
                          },
                        );
                      },
                    ),
                  ),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FutureBuilder<bool>(
        future: Provider.of<AuthController>(context, listen: false).canManage(),
        builder: (context, snapshot) {
          if (snapshot.data == true) {
            return FloatingActionButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const EmployeeFormScreen()),
                ).then((_) => _loadData());
              },
              child: const Icon(Icons.add),
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}
