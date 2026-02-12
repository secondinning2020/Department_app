import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../controllers/auth_controller.dart';
import '../../controllers/department_controller.dart';
import '../../controllers/employee_controller.dart';
import '../../utils/constants.dart';
import '../auth/login_screen.dart';
import '../departments/departments_list_screen.dart';
import '../employees/employees_list_screen.dart';
import '../profile/profile_screen.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final departmentController = Provider.of<DepartmentController>(context, listen: false);
    final employeeController = Provider.of<EmployeeController>(context, listen: false);
    
    await Future.wait([
      departmentController.loadDepartments(),
      employeeController.loadEmployees(),
    ]);
  }

  Future<void> _handleLogout() async {
    final authController = Provider.of<AuthController>(context, listen: false);
    await authController.logout();
    
    if (mounted) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(AppConstants.appName),
        actions: [
          IconButton(
            icon: const Icon(Icons.person),
            onPressed: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const ProfileScreen()),
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _handleLogout,
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(AppConstants.defaultPadding),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Welcome Card
              Consumer<AuthController>(
                builder: (context, authController, child) {
                  return Card(
                    elevation: AppConstants.cardElevation,
                    child: Padding(
                      padding: const EdgeInsets.all(AppConstants.defaultPadding),
                      child: Row(
                        children: [
                          const CircleAvatar(
                            radius: 30,
                            backgroundColor: AppConstants.primaryColor,
                            child: Icon(Icons.person, size: 35, color: Colors.white),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text(
                                  'Welcome back!',
                                  style: TextStyle(
                                    fontSize: 16,
                                    color: Colors.grey,
                                  ),
                                ),
                                Text(
                                  authController.currentUser?.username ?? 'User',
                                  style: AppConstants.subheadingStyle,
                                ),
                                Container(
                                  margin: const EdgeInsets.only(top: 4),
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 8,
                                    vertical: 2,
                                  ),
                                  decoration: BoxDecoration(
                                    color: AppConstants.accentColor.withOpacity(0.2),
                                    borderRadius: BorderRadius.circular(4),
                                  ),
                                  child: Text(
                                    authController.currentUser?.role ?? '',
                                    style: const TextStyle(
                                      fontSize: 12,
                                      fontWeight: FontWeight.bold,
                                      color: AppConstants.accentColor,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
              
              const SizedBox(height: 24),
              
              // Statistics Cards
              Consumer2<DepartmentController, EmployeeController>(
                builder: (context, deptController, empController, child) {
                  return Row(
                    children: [
                      Expanded(
                        child: _buildStatCard(
                          'Departments',
                          deptController.departments.length.toString(),
                          Icons.business,
                          Colors.blue,
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: _buildStatCard(
                          'Employees',
                          empController.employees.length.toString(),
                          Icons.people,
                          Colors.green,
                        ),
                      ),
                    ],
                  );
                },
              ),
              
              const SizedBox(height: 24),
              
              // Quick Actions
              Text(
                'Quick Actions',
                style: AppConstants.subheadingStyle,
              ),
              const SizedBox(height: 16),
              
              _buildActionCard(
                'Manage Departments',
                'View, create, and manage departments',
                Icons.business,
                Colors.blue,
                () {
                  Navigator.of(context).push(
                    MaterialPageRoute(builder: (_) => const DepartmentsListScreen()),
                  );
                },
              ),
              
              const SizedBox(height: 12),
              
              _buildActionCard(
                'Manage Employees',
                'View, create, and manage employees',
                Icons.people,
                Colors.green,
                () {
                  Navigator.of(context).push(
                    MaterialPageRoute(builder: (_) => const EmployeesListScreen()),
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      elevation: AppConstants.cardElevation,
      child: Padding(
        padding: const EdgeInsets.all(AppConstants.defaultPadding),
        child: Column(
          children: [
            Icon(icon, size: 40, color: color),
            const SizedBox(height: 8),
            Text(
              value,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              title,
              style: const TextStyle(
                fontSize: 14,
                color: Colors.grey,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActionCard(
    String title,
    String subtitle,
    IconData icon,
    Color color,
    VoidCallback onTap,
  ) {
    return Card(
      elevation: AppConstants.cardElevation,
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: color.withOpacity(0.2),
          child: Icon(icon, color: color),
        ),
        title: Text(
          title,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(subtitle),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: onTap,
      ),
    );
  }
}
