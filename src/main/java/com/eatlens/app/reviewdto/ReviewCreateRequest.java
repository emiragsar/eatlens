package com.eatlens.app.reviewdto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReviewCreateRequest {
    
    @NotNull(message = "Restoran ID zorunludur")
    private Long restaurantId;
    
    @NotNull(message = "Puan zorunludur")
    @Min(value = 1, message = "Puan en az 1 olmalıdır")
    @Max(value = 5, message = "Puan en fazla 5 olmalıdır")
    private Integer rating;
    
    private String comment;
    
    @Min(1) @Max(5)
    private Integer foodRating;
    
    @Min(1) @Max(5)
    private Integer serviceRating;
    
    @Min(1) @Max(5)
    private Integer ambianceRating;
    
    @Min(1) @Max(5)
    private Integer priceRating;
    
    private List<String> imageUrls;
}