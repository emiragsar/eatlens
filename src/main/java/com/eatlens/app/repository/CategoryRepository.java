package com.eatlens.app.repository;

import com.eatlens.app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByRestaurantIdAndIsActiveTrue(Long restaurantId);

    List<Category> findByRestaurantIdOrderByDisplayOrder(Long restaurantId);

    boolean existsByNameAndRestaurantId(String name, Long restaurantId);
}

