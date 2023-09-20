package com.example.employeemanagementservice.service;

import com.example.employeemanagementservice.domain.Employee;
import com.example.employeemanagementservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final KafkaTemplate<String, String> kafkaTemplate; // KafkaTemplate for publishing events

    private final String employeeTopic = "employee-events"; // Kafka topic for employee events

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(UUID uuid) {
        return employeeRepository.findByUuid(uuid);
    }

    public Employee createEmployee(Employee employee) {
        employee.setFullName(employee.getFullName());
        Employee savedEmployee = employeeRepository.save(employee);

        // Publish an event when an employee is created
        publishEmployeeEvent("EmployeeCreated", savedEmployee);

        return savedEmployee;
    }

    public Employee updateEmployee(UUID uuid, Employee updatedEmployee) {
        Optional<Employee> existingEmployeeOptional = employeeRepository.findByUuid(uuid);

        if (existingEmployeeOptional.isPresent()) {
            Employee existingEmployee = existingEmployeeOptional.get();
            existingEmployee.setEmail(updatedEmployee.getEmail());
            existingEmployee.setFirstName(updatedEmployee.getFirstName());
            existingEmployee.setLastName(updatedEmployee.getLastName());
            existingEmployee.setBirthday(updatedEmployee.getBirthday());
            existingEmployee.setFullName(updatedEmployee.getFullName());
            existingEmployee.setHobby(updatedEmployee.getHobby());

            Employee updatedEmployeeEntity = employeeRepository.save(existingEmployee);

            // Publish an event when an employee is updated
            publishEmployeeEvent("EmployeeUpdated", updatedEmployeeEntity);

            return updatedEmployeeEntity;
        } else {
            return null;
        }
    }

    public void deleteEmployee(UUID uuid) {
        employeeRepository.findByUuid(uuid).ifPresent(employee -> {
            employeeRepository.delete(employee);

            // Publish an event when an employee is deleted
            publishEmployeeEvent("EmployeeDeleted", employee);
        });
    }

    // Publish an employee event to Kafka
    private void publishEmployeeEvent(String eventType, Employee employee) {
        String eventMessage = eventType + " - Employee ID: " + employee.getUuid();
        kafkaTemplate.send(employeeTopic, eventMessage);
        log.info("Published event: {}", eventMessage);
    }
}
