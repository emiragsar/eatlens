package com.eatlens.app.restaurantdto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantCreateRequest {
    
    @NotBlank(message = "Restoran adı zorunludur")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Adres zorunludur")
    private String address;
    
    @NotBlank(message = "Şehir zorunludur")
    private String city;
    
    private String district;
    private String postalCode;
    
    @NotNull(message = "Konum bilgisi zorunludur")
    private Double latitude;
    
    @NotNull(message = "Konum bilgisi zorunludur")
    private Double longitude;
    
    @NotBlank(message = "Telefon numarası zorunludur")
    private String phoneNumber;
    
    private String email;
    private String website;
    private String openingHours; // JSON string
    private String cuisineType;
    private Integer priceRange; // 1-5
    private String logoUrl;
    private String coverImageUrl;
}