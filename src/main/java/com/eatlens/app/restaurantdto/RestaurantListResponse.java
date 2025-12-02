package com.eatlens.app.restaurantdto;


import lombok.Data;

@Data
public class RestaurantListResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String district;
    private String cuisineType;
    private Integer priceRange;
    private Double averageRating;
    private Integer totalReviews;
    private String logoUrl;
    private Double distance; // Konum bazlı aramalarda kullanılır
    private Boolean isOpen; // Şu an açık mı
}
