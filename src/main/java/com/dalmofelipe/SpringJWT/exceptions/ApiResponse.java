package com.dalmofelipe.SpringJWT.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApiResponse {

    private LocalDateTime now = LocalDateTime.now();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private String timestamp = now.format(formatter);
    private Integer statusCode;
    private Object details;

    public ApiResponse(Integer status, Object details) {
        this.statusCode = status;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ApiResponse [timestamp=" + timestamp + ", statusCode=" + statusCode + ", details=" + details + "]";
    }    
}
