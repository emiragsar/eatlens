package com.eatlens.app.dto.searchdto;

import lombok.Data;

@Data
public class RestaurantSearchRequest {
    private String keyword;
    private String city;
    private String district;
    private String cuisineType;
    private Integer minPrice;
    private Integer maxPrice;
    private Double minRating;
    private Double latitude;
    private Double longitude;
    private Double radius; // km cinsinden
    private String sortBy; // distance, rating, price
    private String sortDirection; // ASC, DESC
    private Integer page = 0;
    private Integer size = 20;
}