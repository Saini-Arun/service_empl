package net.javaguides.employeeservice.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import net.javaguides.employeeservice.dto.APIResponseDto;
import net.javaguides.employeeservice.dto.DepartmentDto;
import net.javaguides.employeeservice.dto.EmployeeDto;
import net.javaguides.employeeservice.dto.OrganizationDto;
import net.javaguides.employeeservice.entity.Employee;
import net.javaguides.employeeservice.exception.EmailAlreadyRegisteredException;
import net.javaguides.employeeservice.exception.ResourceNotFoundException;
import net.javaguides.employeeservice.repository.EmployeeRepository;
import net.javaguides.employeeservice.service.APIClient;
import net.javaguides.employeeservice.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    private ModelMapper modelMapper;
//    private RestTemplate restTemplate;
    private WebClient webClient;
//    private APIClient apiClient;

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        if(employeeRepository.existsByEmail(employeeDto.getEmail())){
            throw new EmailAlreadyRegisteredException("Email Already Registered.");
        }
//        Employee employee=new Employee(
//                employeeDto.getId(),
//                employeeDto.getFirstName(),
//                employeeDto.getLastName(),
//                employeeDto.getEmail()
//        );
        Employee employee=modelMapper.map(employeeDto, Employee.class);
        Employee savedResponse= employeeRepository.save(employee);
//        EmployeeDto savedResponseDto=new EmployeeDto(
//                savedResponse.getId(),
//                savedResponse.getFirstName(),
//                savedResponse.getLastName(),
//                savedResponse.getEmail()
//        );
        EmployeeDto savedResponseDto=modelMapper.map(savedResponse,EmployeeDto.class);
        return savedResponseDto;
    }

    @Override
    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    public APIResponseDto getEmployeeById(Long id) {
        if(!employeeRepository.existsById(id)){
            throw new ResourceNotFoundException("Employee","ID",id);
        }
        Employee response=employeeRepository.findById(id).get();
        /* Call to Department Service via RestTemplate. */
//        ResponseEntity<DepartmentDto> department=restTemplate.getForEntity("http://localhost:8080/api/departments/"+response.getDepartmentCode()
//        ,DepartmentDto.class);
        /* Call to Department Service via webClient. */
        DepartmentDto departmentDto=webClient.get().uri("http://localhost:8080/api/departments/"+response.getDepartmentCode()).retrieve().bodyToMono(DepartmentDto.class).block();
        OrganizationDto organizationDto=webClient.get().uri("http://localhost:8083/api/organizations/"+response.getOrganizationCode()).retrieve().bodyToMono(OrganizationDto.class).block();
        /* Call to Department Service via OpenFeign. */
//        DepartmentDto departmentDto=apiClient.getDepartmentByCode(response.getDepartmentCode());
//        EmployeeDto responseDto=new EmployeeDto(
//                response.getId(),
//                response.getFirstName(),
//                response.getLastName(),
//                response.getEmail()
//        );
        EmployeeDto responseDto=modelMapper.map(response,EmployeeDto.class);
        APIResponseDto apiResponseDto=new APIResponseDto();
        apiResponseDto.setEmployeeDto(responseDto);
        apiResponseDto.setDepartmentDto(departmentDto);
        apiResponseDto.setOrganizationDto(organizationDto);
        return apiResponseDto;
    }

    public APIResponseDto getDefaultDepartment(Long id, Throwable throwable) {
        if(!employeeRepository.existsById(id)){
            throw new ResourceNotFoundException("Employee","ID",id);
        }
        Employee response=employeeRepository.findById(id).get();
        DepartmentDto departmentDto=new DepartmentDto(
                Long.MIN_VALUE,
                "Default Name",
                "Default Description",
                "ABXXX"
        );
        EmployeeDto responseDto=modelMapper.map(response,EmployeeDto.class);
        APIResponseDto apiResponseDto=new APIResponseDto();
        apiResponseDto.setEmployeeDto(responseDto);
        apiResponseDto.setDepartmentDto(departmentDto);
        return apiResponseDto;
    }
}
