package com.eatlens.app.dto.categorydto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequest {
    
    @NotBlank(message = "Kategori adı zorunludur")
    private String name;
    
    private String description;
    private Integer displayOrder;
}