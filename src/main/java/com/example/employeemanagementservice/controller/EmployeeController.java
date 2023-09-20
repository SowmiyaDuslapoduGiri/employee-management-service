package com.example.employeemanagementservice.controller;

import com.example.employeemanagementservice.domain.Employee;
import com.example.employeemanagementservice.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/employees")
@Api(tags = "Employee Management")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees")
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        log.info("Fetched {} employees", employees.size());
        return employees;
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get an employee by UUID")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID uuid) {
        log.info("Fetching employee by UUID: {}", uuid);
        Optional<Employee> employee = employeeService.getEmployeeById(uuid);
        if (employee.isPresent()) {
            log.info("Found employee: {}", employee.get());
            return ResponseEntity.ok(employee.get());
        } else {
            log.warn("Employee not found with UUID: {}", uuid);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create a new employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        log.info("Creating a new employee: {}", employee);
        Employee createdEmployee = employeeService.createEmployee(employee);
        log.info("Created employee with UUID: {}", createdEmployee.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update an employee by UUID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable UUID uuid, @RequestBody Employee updatedEmployee) {
        log.info("Updating employee with UUID: {}", uuid);
        Employee employee = employeeService.updateEmployee(uuid, updatedEmployee);
        if (employee != null) {
            log.info("Updated employee: {}", employee);
            return ResponseEntity.ok(employee);
        } else {
            log.warn("Employee not found with UUID: {}", uuid);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete an employee by UUID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID uuid) {
        log.info("Deleting employee with UUID: {}", uuid);
        employeeService.deleteEmployee(uuid);
        log.info("Deleted employee with UUID: {}", uuid);
        return ResponseEntity.noContent().build();
    }
}
