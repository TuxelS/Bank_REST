package com.example.bankcards.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ApplicationError {
    private int status;
    private String message;
    private Date timestamp;

    public ApplicationError(int status, String message){
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
