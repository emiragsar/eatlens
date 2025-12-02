package com.eatlens.app.dto.menuitemdto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemCreateRequest {
    
    @NotBlank(message = "Ürün adı zorunludur")
    private String name;
    
    private String description;
    
    @NotNull(message = "Fiyat zorunludur")
    @Positive(message = "Fiyat pozitif olmalıdır")
    private BigDecimal price;
    
    @NotNull(message = "Kategori ID zorunludur")
    private Long categoryId;
    
    private Integer preparationTime;
    private Integer calories;
    private String ingredients;
    private String allergenInfo;
    private String imageUrl;
    private String servingSize;
    private Boolean isVegetarian = false;
    private Boolean isVegan = false;
    private Boolean isGlutenFree = false;
}