import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../controllers/department_controller.dart';
import '../../controllers/employee_controller.dart';
import '../../models/employee_model.dart';
import '../../utils/constants.dart';
import '../../utils/validators.dart';

class EmployeeFormScreen extends StatefulWidget {
  final Employee? employee;
  
  const EmployeeFormScreen({super.key, this.employee});

  @override
  State<EmployeeFormScreen> createState() => _EmployeeFormScreenState();
}

class _EmployeeFormScreenState extends State<EmployeeFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _emailController;
  late TextEditingController _positionController;
  late TextEditingController _salaryController;
  String? _selectedDepartmentId;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.employee?.name ?? '');
    _emailController = TextEditingController(text: widget.employee?.email ?? '');
    _positionController = TextEditingController(text: widget.employee?.position ?? '');
    _salaryController = TextEditingController(text: widget.employee?.salary.toString() ?? '');
    _selectedDepartmentId = widget.employee?.departmentId;
    
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<DepartmentController>(context, listen: false).loadDepartments();
    });
  }

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _positionController.dispose();
    _salaryController.dispose();
    super.dispose();
  }

  Future<void> _handleSubmit() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    if (_selectedDepartmentId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please select a department'),
          backgroundColor: AppConstants.errorColor,
        ),
      );
      return;
    }

    final controller = Provider.of<EmployeeController>(context, listen: false);
    final employee = Employee(
      id: widget.employee?.id,
      name: _nameController.text.trim(),
      email: _emailController.text.trim(),
      position: _positionController.text.trim(),
      salary: double.parse(_salaryController.text.trim()),
      departmentId: _selectedDepartmentId!,
    );

    bool success;
    if (widget.employee == null) {
      success = await controller.createEmployee(_selectedDepartmentId!, employee);
    } else {
      success = await controller.updateEmployee(
        widget.employee!.departmentId,
        widget.employee!.id!,
        employee,
      );
    }

    if (mounted) {
      if (success) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(widget.employee == null ? 'Employee created' : 'Employee updated'),
            backgroundColor: AppConstants.successColor,
          ),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(controller.errorMessage ?? 'Operation failed'),
            backgroundColor: AppConstants.errorColor,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.employee == null ? 'Add Employee' : 'Edit Employee'),
      ),
      body: Consumer2<EmployeeController, DepartmentController>(
        builder: (context, empController, deptController, child) {
          return SingleChildScrollView(
            padding: const EdgeInsets.all(AppConstants.defaultPadding),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  TextFormField(
                    controller: _nameController,
                    decoration: InputDecoration(
                      labelText: 'Full Name',
                      prefixIcon: const Icon(Icons.person),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      ),
                    ),
                    validator: (value) => Validators.validateRequired(value, 'Name'),
                    textInputAction: TextInputAction.next,
                  ),
                  const SizedBox(height: 16),
                  
                  TextFormField(
                    controller: _emailController,
                    decoration: InputDecoration(
                      labelText: 'Email',
                      prefixIcon: const Icon(Icons.email),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      ),
                    ),
                    validator: Validators.validateEmail,
                    keyboardType: TextInputType.emailAddress,
                    textInputAction: TextInputAction.next,
                  ),
                  const SizedBox(height: 16),
                  
                  TextFormField(
                    controller: _positionController,
                    decoration: InputDecoration(
                      labelText: 'Position',
                      prefixIcon: const Icon(Icons.work),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      ),
                    ),
                    validator: (value) => Validators.validateRequired(value, 'Position'),
                    textInputAction: TextInputAction.next,
                  ),
                  const SizedBox(height: 16),
                  
                  TextFormField(
                    controller: _salaryController,
                    decoration: InputDecoration(
                      labelText: 'Salary',
                      prefixIcon: const Icon(Icons.attach_money),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      ),
                    ),
                    validator: (value) => Validators.validateNumber(value, 'Salary'),
                    keyboardType: TextInputType.number,
                    textInputAction: TextInputAction.next,
                  ),
                  const SizedBox(height: 16),
                  
                  DropdownButtonFormField<String>(
                    value: _selectedDepartmentId,
                    decoration: InputDecoration(
                      labelText: 'Department',
                      prefixIcon: const Icon(Icons.business),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      ),
                    ),
                    items: deptController.departments.map((dept) {
                      return DropdownMenuItem(
                        value: dept.id,
                        child: Text(dept.name),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() {
                        _selectedDepartmentId = value;
                      });
                    },
                    validator: (value) {
                      if (value == null) {
                        return 'Please select a department';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 24),
                  
                  ElevatedButton(
                    onPressed: empController.isLoading ? null : _handleSubmit,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppConstants.primaryColor,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(AppConstants.borderRadius),
                      ),
                    ),
                    child: empController.isLoading
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                            ),
                          )
                        : Text(
                            widget.employee == null ? 'Create Employee' : 'Update Employee',
                            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                          ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
