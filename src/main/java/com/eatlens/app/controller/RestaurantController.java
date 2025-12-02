package com.eatlens.app.controller;


import com.eatlens.app.dto.restaurantdto.RestaurantCreateRequest;
import com.eatlens.app.dto.restaurantdto.RestaurantListResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantUpdateRequest;
import com.eatlens.app.dto.searchdto.NearbyRestaurantRequest;
import com.eatlens.app.dto.searchdto.RestaurantSearchRequest;
import com.eatlens.app.dto.userdto.BaseResponse;
import com.eatlens.app.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.eatlens.app.dto.restaurantdto.RestaurantResponse;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @RequestHeader("userId") Long userId,
            @Valid @RequestBody RestaurantCreateRequest request) {
        RestaurantResponse response = restaurantService.createRestaurant(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId,
            @Valid @RequestBody RestaurantUpdateRequest request) {
        RestaurantResponse response = restaurantService.updateRestaurant(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantListResponse>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RestaurantListResponse> response = restaurantService.getAllRestaurants(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<RestaurantListResponse>> searchRestaurants(
            @RequestBody RestaurantSearchRequest request) {
        Page<RestaurantListResponse> response = restaurantService.searchRestaurants(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nearby")
    public ResponseEntity<List<RestaurantListResponse>> getNearbyRestaurants(
            @Valid @RequestBody NearbyRestaurantRequest request) {
        List<RestaurantListResponse> response = restaurantService.getNearbyRestaurants(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-restaurants")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(
            @RequestHeader("userId") Long userId) {
        List<RestaurantResponse> response = restaurantService.getOwnerRestaurants(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<BaseResponse> deleteRestaurant(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId) {
        restaurantService.deleteRestaurant(id, userId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Restoran başarıyla silindi");
        return ResponseEntity.ok(response);
    }
}