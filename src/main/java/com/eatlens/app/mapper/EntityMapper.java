package com.eatlens.app.mapper;

import com.eatlens.app.dto.categorydto.CategoryCreateRequest;
import com.eatlens.app.dto.categorydto.CategoryResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemCreateRequest;
import com.eatlens.app.dto.menuitemdto.MenuItemResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantCreateRequest;
import com.eatlens.app.dto.restaurantdto.RestaurantListResponse;
import com.eatlens.app.dto.restaurantdto.RestaurantResponse;
import com.eatlens.app.dto.reviewdto.ReviewCreateRequest;
import com.eatlens.app.dto.reviewdto.ReviewResponse;
import com.eatlens.app.dto.userdto.UserRegisterRequest;
import com.eatlens.app.dto.userdto.UserResponse;
import com.eatlens.app.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class EntityMapper {
    private final ObjectMapper objectMapper;

    public EntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    // User Mappings
    public User toUser(UserRegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Şifrelenecek
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setIsActive(true);
        return user;
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole().toString());
        response.setIsActive(user.getIsActive());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    // Restaurant Mappings
    public Restaurant toRestaurant(RestaurantCreateRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setDistrict(request.getDistrict());
        restaurant.setPostalCode(request.getPostalCode());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setPhoneNumber(request.getPhoneNumber());
        restaurant.setEmail(request.getEmail());
        restaurant.setWebsite(request.getWebsite());
        restaurant.setOpeningHours(request.getOpeningHours());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setPriceRange(request.getPriceRange());
        restaurant.setLogoUrl(request.getLogoUrl());
        restaurant.setCoverImageUrl(request.getCoverImageUrl());
        restaurant.setIsActive(true);
        restaurant.setAverageRating(0.0);
        restaurant.setTotalReviews(0);
        return restaurant;
    }

    public RestaurantResponse toRestaurantResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setDescription(restaurant.getDescription());
        response.setAddress(restaurant.getAddress());
        response.setCity(restaurant.getCity());
        response.setDistrict(restaurant.getDistrict());
        response.setLatitude(restaurant.getLatitude());
        response.setLongitude(restaurant.getLongitude());
        response.setPhoneNumber(restaurant.getPhoneNumber());
        response.setEmail(restaurant.getEmail());
        response.setWebsite(restaurant.getWebsite());
        response.setOpeningHours(restaurant.getOpeningHours());
        response.setCuisineType(restaurant.getCuisineType());
        response.setPriceRange(restaurant.getPriceRange());
        response.setAverageRating(restaurant.getAverageRating());
        response.setTotalReviews(restaurant.getTotalReviews());
        response.setIsActive(restaurant.getIsActive());
        response.setLogoUrl(restaurant.getLogoUrl());
        response.setCoverImageUrl(restaurant.getCoverImageUrl());
        if (restaurant.getOwner() != null) {
            response.setOwner(toUserResponse(restaurant.getOwner()));
        }
        response.setCreatedAt(restaurant.getCreatedAt());
        return response;
    }

    public RestaurantListResponse toRestaurantListResponse(Restaurant restaurant) {
        RestaurantListResponse response = new RestaurantListResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setAddress(restaurant.getAddress());
        response.setCity(restaurant.getCity());
        response.setDistrict(restaurant.getDistrict());
        response.setCuisineType(restaurant.getCuisineType());
        response.setPriceRange(restaurant.getPriceRange());
        response.setAverageRating(restaurant.getAverageRating());
        response.setTotalReviews(restaurant.getTotalReviews());
        response.setLogoUrl(restaurant.getLogoUrl());
        return response;
    }
    public Category toCategory(CategoryCreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setIsActive(true);
        return category;
    }

    public CategoryResponse toCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setIsActive(category.getIsActive());

        // Calculate item count
        if (category.getMenuItems() != null) {
            response.setItemCount((int) category.getMenuItems().stream()
                    .filter(item -> item.getIsAvailable() && !item.getIsDeleted())
                    .count());
        } else {
            response.setItemCount(0);
        }

        return response;
    }

    // MenuItem Mappings
    public MenuItem toMenuItem(MenuItemCreateRequest request) {
        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setPreparationTime(request.getPreparationTime());
        item.setCalories(request.getCalories());
        item.setIngredients(request.getIngredients());
        item.setAllergenInfo(request.getAllergenInfo());
        item.setImageUrl(request.getImageUrl());
        item.setServingSize(request.getServingSize());
        item.setIsVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false);
        item.setIsVegan(request.getIsVegan() != null ? request.getIsVegan() : false);
        item.setIsGlutenFree(request.getIsGlutenFree() != null ? request.getIsGlutenFree() : false);
        return item;
    }

    public MenuItemResponse toMenuItemResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setPreparationTime(item.getPreparationTime());
        response.setCalories(item.getCalories());
        response.setIngredients(item.getIngredients());
        response.setAllergenInfo(item.getAllergenInfo());
        response.setImageUrl(item.getImageUrl());
        response.setIsAvailable(item.getIsAvailable());
        response.setIsPopular(item.getIsPopular());
        response.setIsVegetarian(item.getIsVegetarian());
        response.setIsVegan(item.getIsVegan());
        response.setIsGlutenFree(item.getIsGlutenFree());
        response.setServingSize(item.getServingSize());

        if (item.getCategory() != null) {
            response.setCategory(toCategoryResponse(item.getCategory()));
        }

        return response;
    }

    // Review Mappings
    public Review toReview(ReviewCreateRequest request) {
        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setFoodRating(request.getFoodRating());
        review.setServiceRating(request.getServiceRating());
        review.setAmbianceRating(request.getAmbianceRating());
        review.setPriceRating(request.getPriceRating());
        return review;
    }

    public ReviewResponse toReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());

        if (review.getCustomer() != null) {
            response.setCustomer(toUserResponse(review.getCustomer()));
        }

        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setFoodRating(review.getFoodRating());
        response.setServiceRating(review.getServiceRating());
        response.setAmbianceRating(review.getAmbianceRating());
        response.setPriceRating(review.getPriceRating());
        response.setIsVerifiedPurchase(review.getIsVerifiedPurchase());
        response.setHelpfulCount(review.getHelpfulCount());
        response.setOwnerResponse(review.getOwnerResponse());
        response.setCreatedAt(review.getCreatedAt());

        // Parse image URLs from JSON
        if (review.getImageUrls() != null && !review.getImageUrls().isEmpty()) {
            try {
                List<String> urls = objectMapper.readValue(review.getImageUrls(), new TypeReference<>() {});
                response.setImageUrls(urls);
            } catch (Exception e) {
                response.setImageUrls(Collections.emptyList());
            }
        } else {
            response.setImageUrls(Collections.emptyList());
        }

        return response;
    }

    // Diğer mapping metodları...
}