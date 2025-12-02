package com.eatlens.app.service.impl;

import com.eatlens.app.dto.restaurantdto.RestaurantCreateRequest;
import com.eatlens.app.dto.restaurantdto.RestaurantListResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantUpdateRequest;
import com.eatlens.app.dto.searchdto.NearbyRestaurantRequest;
import com.eatlens.app.dto.searchdto.RestaurantSearchRequest;
import com.eatlens.app.exception.BusinessException;
import com.eatlens.app.exception.ResourceNotFoundException;
import com.eatlens.app.mapper.EntityMapper;
import com.eatlens.app.model.Restaurant;
import com.eatlens.app.model.User;
import com.eatlens.app.repository.RestaurantRepository;
import com.eatlens.app.repository.ReviewRepository;
import com.eatlens.app.repository.UserRepository;
import com.eatlens.app.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final EntityMapper mapper;

    @Override
    public RestaurantResponse createRestaurant(RestaurantCreateRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        if (!"RESTAURANT_OWNER".equals(owner.getRole().toString())) {
            throw new BusinessException("Bu kullanıcı restoran oluşturamaz");
        }

        Restaurant restaurant = mapper.toRestaurant(request);
        restaurant.setOwner(owner);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created: {} by owner: {}", savedRestaurant.getId(), ownerId);

        return mapper.toRestaurantResponse(savedRestaurant);
    }

    @Override
    public RestaurantResponse updateRestaurant(Long id, RestaurantUpdateRequest request, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new BusinessException("Bu restoranı güncelleme yetkiniz yok");
        }

        // Update fields
        if (request.getName() != null) restaurant.setName(request.getName());
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getAddress() != null) restaurant.setAddress(request.getAddress());
        if (request.getCity() != null) restaurant.setCity(request.getCity());
        if (request.getDistrict() != null) restaurant.setDistrict(request.getDistrict());
        if (request.getPhoneNumber() != null) restaurant.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) restaurant.setEmail(request.getEmail());
        if (request.getWebsite() != null) restaurant.setWebsite(request.getWebsite());
        if (request.getOpeningHours() != null) restaurant.setOpeningHours(request.getOpeningHours());
        if (request.getCuisineType() != null) restaurant.setCuisineType(request.getCuisineType());
        if (request.getPriceRange() != null) restaurant.setPriceRange(request.getPriceRange());
        if (request.getLogoUrl() != null) restaurant.setLogoUrl(request.getLogoUrl());
        if (request.getCoverImageUrl() != null) restaurant.setCoverImageUrl(request.getCoverImageUrl());
        if (request.getIsActive() != null) restaurant.setIsActive(request.getIsActive());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant updated: {}", id);

        return mapper.toRestaurantResponse(updatedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        return mapper.toRestaurantResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantListResponse> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending());
        Page<Restaurant> restaurants = restaurantRepository.findByIsActiveTrue(pageable);

        return restaurants.map(mapper::toRestaurantListResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantListResponse> searchRestaurants(RestaurantSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSortBy() != null ? request.getSortBy() : "averageRating")
                        .descending()
        );

        Page<Restaurant> restaurants;

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            restaurants = restaurantRepository.searchRestaurants(request.getKeyword(), pageable);
        } else {
            restaurants = restaurantRepository.findWithFilters(
                    request.getCity(),
                    request.getDistrict(),
                    request.getCuisineType(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getMinRating(),
                    pageable
            );
        }

        return restaurants.map(mapper::toRestaurantListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantListResponse> getNearbyRestaurants(NearbyRestaurantRequest request) {
        List<Restaurant> nearbyRestaurants = restaurantRepository.findNearbyRestaurants(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius()
        );

        return nearbyRestaurants.stream()
                .map(mapper::toRestaurantListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getOwnerRestaurants(Long ownerId) {
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);

        return restaurants.stream()
                .map(mapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRestaurant(Long id, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new BusinessException("Bu restoranı silme yetkiniz yok");
        }

        restaurant.setIsActive(false);
        restaurant.setIsDeleted(true);
        restaurantRepository.save(restaurant);

        log.info("Restaurant deleted: {}", id);
    }

    @Override
    public void updateRestaurantRating(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        Double avgRating = reviewRepository.calculateAverageRating(restaurantId);
        Integer totalReviews = reviewRepository.countReviewsByRestaurant(restaurantId);

        restaurant.setAverageRating(avgRating != null ? avgRating : 0.0);
        restaurant.setTotalReviews(totalReviews != null ? totalReviews : 0);

        restaurantRepository.save(restaurant);
        log.info("Restaurant rating updated: {} - Avg: {}, Total: {}", restaurantId, avgRating, totalReviews);
    }
}
