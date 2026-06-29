package com.edu.cms.common.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiResponse <T> {
    private boolean success;
    private String message;
    private T data;
    private T error;
    private HttpStatus status;
}
