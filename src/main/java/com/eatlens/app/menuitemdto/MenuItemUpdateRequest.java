package com.eatlens.app.menuitemdto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Integer preparationTime;
    private Integer calories;
    private String ingredients;
    private String allergenInfo;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isPopular;
    private String servingSize;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
}