package com.eatlens.app.dto.searchdto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, String> validationErrors;
}
