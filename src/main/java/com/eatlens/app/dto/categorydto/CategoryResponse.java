package com.eatlens.app.dto.categorydto;


import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private Integer itemCount; // Bu kategorideki ürün sayısı
}
