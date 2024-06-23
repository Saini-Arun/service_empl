package net.javaguides.employeeservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmailAlreadyRegisteredException extends RuntimeException{

    private String message;

    public EmailAlreadyRegisteredException(String message){
        super(message);
        this.message=message;
    }
}
