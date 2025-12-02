package com.eatlens.app.restaurantdto;


import lombok.Data;

@Data
public class RestaurantUpdateRequest {
    private String name;
    private String description;
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String website;
    private String openingHours;
    private String cuisineType;
    private Integer priceRange;
    private String logoUrl;
    private String coverImageUrl;
    private Boolean isActive;
}
