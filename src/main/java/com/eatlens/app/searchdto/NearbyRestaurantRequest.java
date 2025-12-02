package com.eatlens.app.searchdto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NearbyRestaurantRequest {
    
    @NotNull(message = "Enlem bilgisi zorunludur")
    private Double latitude;
    
    @NotNull(message = "Boylam bilgisi zorunludur")
    private Double longitude;
    
    private Double radius = 5.0; // Varsayılan 5 km
    private Integer page = 0;
    private Integer size = 20;
}