package com.eatlens.app.menuitemdto;


import lombok.Data;

import java.math.BigDecimal;

import com.eatlens.app.categorydto.CategoryResponse;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer preparationTime;
    private Integer calories;
    private String ingredients;
    private String allergenInfo;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isPopular;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private String servingSize;
    private CategoryResponse category;
}