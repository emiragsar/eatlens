package com.eatlens.app.service;

import com.eatlens.app.dto.restaurantdto.RestaurantCreateRequest;
import com.eatlens.app.dto.restaurantdto.RestaurantListResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantUpdateRequest;
import com.eatlens.app.dto.searchdto.NearbyRestaurantRequest;
import com.eatlens.app.dto.searchdto.RestaurantSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RestaurantService {
    RestaurantResponse createRestaurant(RestaurantCreateRequest request, Long ownerId);
    RestaurantResponse updateRestaurant(Long id, RestaurantUpdateRequest request, Long ownerId);
    RestaurantResponse getRestaurantById(Long id);
    Page<RestaurantListResponse> getAllRestaurants(int page, int size);
    Page<RestaurantListResponse> searchRestaurants(RestaurantSearchRequest request);
    List<RestaurantListResponse> getNearbyRestaurants(NearbyRestaurantRequest request);
    List<RestaurantResponse> getOwnerRestaurants(Long ownerId);
    void deleteRestaurant(Long id, Long ownerId);
    void updateRestaurantRating(Long restaurantId);
}
