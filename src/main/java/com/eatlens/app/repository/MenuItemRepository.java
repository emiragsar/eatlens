package com.eatlens.app.repository;

import com.eatlens.app.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);

    List<MenuItem> findByCategoryId(Long categoryId);

    List<MenuItem> findByCategoryIdAndIsAvailableTrue(Long categoryId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId " +
            "AND m.isPopular = true AND m.isAvailable = true")
    List<MenuItem> findPopularItems(@Param("restaurantId") Long restaurantId);

    // Diyet filtresi
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId " +
            "AND (:vegetarian IS NULL OR m.isVegetarian = :vegetarian) " +
            "AND (:vegan IS NULL OR m.isVegan = :vegan) " +
            "AND (:glutenFree IS NULL OR m.isGlutenFree = :glutenFree) " +
            "AND m.isAvailable = true")
    List<MenuItem> findWithDietaryFilters(@Param("restaurantId") Long restaurantId,
                                          @Param("vegetarian") Boolean vegetarian,
                                          @Param("vegan") Boolean vegan,
                                          @Param("glutenFree") Boolean glutenFree);

    // Menü araması
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId " +
            "AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND m.isAvailable = true")
    Page<MenuItem> searchMenuItems(@Param("restaurantId") Long restaurantId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);
}
