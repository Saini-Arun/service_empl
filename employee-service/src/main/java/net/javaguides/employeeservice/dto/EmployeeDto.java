package net.javaguides.employeeservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;
    @NotEmpty(message = "First name should not be null or empty.")
    private String firstName;
    @NotEmpty(message = "Last name should not be null or empty.")
    private String lastName;
    @NotEmpty(message = "Email should not be null or empty.")
    @Email(message = "Enter a valid email.")
    private String email;
    private String departmentCode;
    private String organizationCode;
}
