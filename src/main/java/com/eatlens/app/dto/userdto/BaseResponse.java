package com.eatlens.app.dto.userdto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}