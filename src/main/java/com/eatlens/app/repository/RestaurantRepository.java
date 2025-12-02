package com.eatlens.app.repository;

import com.eatlens.app.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findByIsActiveTrue(Pageable pageable);

    Page<Restaurant> findByCityAndIsActiveTrue(String city, Pageable pageable);

    Optional<Restaurant> findByIdAndIsActiveTrue(Long id);

    List<Restaurant> findByOwnerId(Long ownerId);

    // Metin araması
    @Query("SELECT r FROM Restaurant r WHERE " +
            "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND r.isActive = true")
    Page<Restaurant> searchRestaurants(@Param("keyword") String keyword, Pageable pageable);

    // Yakındaki restoranları bul (Haversine formula)
    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(r.latitude)))) AS distance " +
            "FROM restaurants r " +
            "WHERE r.is_active = true " +
            "HAVING distance < :radius " +
            "ORDER BY distance",
            nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(@Param("lat") Double latitude,
                                           @Param("lng") Double longitude,
                                           @Param("radius") Double radius);

    // Filtreleme
    @Query("SELECT r FROM Restaurant r WHERE " +
            "(:city IS NULL OR r.city = :city) AND " +
            "(:district IS NULL OR r.district = :district) AND " +
            "(:cuisineType IS NULL OR r.cuisineType = :cuisineType) AND " +
            "(:minPrice IS NULL OR r.priceRange >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.priceRange <= :maxPrice) AND " +
            "(:minRating IS NULL OR r.averageRating >= :minRating) AND " +
            "r.isActive = true")
    Page<Restaurant> findWithFilters(@Param("city") String city,
                                     @Param("district") String district,
                                     @Param("cuisineType") String cuisineType,
                                     @Param("minPrice") Integer minPrice,
                                     @Param("maxPrice") Integer maxPrice,
                                     @Param("minRating") Double minRating,
                                     Pageable pageable);
}