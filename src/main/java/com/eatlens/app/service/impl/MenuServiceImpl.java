package com.eatlens.app.service.impl;

import com.eatlens.app.dto.categorydto.CategoryCreateRequest;
import com.eatlens.app.dto.categorydto.CategoryResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemCreateRequest;
import com.eatlens.app.dto.menuitemdto.MenuItemResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemUpdateRequest;

import com.eatlens.app.exception.BusinessException;
import com.eatlens.app.exception.ResourceNotFoundException;
import com.eatlens.app.mapper.EntityMapper;
import com.eatlens.app.model.Category;
import com.eatlens.app.model.MenuItem;
import com.eatlens.app.model.Restaurant;
import com.eatlens.app.repository.*;
import com.eatlens.app.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuServiceImpl implements MenuService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final EntityMapper mapper;

    @Override
    public CategoryResponse createCategory(Long restaurantId, CategoryCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        if (categoryRepository.existsByNameAndRestaurantId(request.getName(), restaurantId)) {
            throw new BusinessException("Bu kategori zaten mevcut");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setRestaurant(restaurant);
        category.setIsActive(true);

        Category saved = categoryRepository.save(category);
        log.info("Category created: {} for restaurant: {}", saved.getId(), restaurantId);

        return mapper.toCategoryResponse(saved);
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı"));

        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());

        Category updated = categoryRepository.save(category);
        return mapper.toCategoryResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRestaurantCategories(Long restaurantId) {
        List<Category> categories = categoryRepository.findByRestaurantIdOrderByDisplayOrder(restaurantId);

        return categories.stream()
                .map(mapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı"));

        // Check if category has items
        List<MenuItem> items = menuItemRepository.findByCategoryId(categoryId);
        if (!items.isEmpty()) {
            throw new BusinessException("Bu kategoride ürünler var, önce ürünleri silin");
        }

        category.setIsActive(false);
        category.setIsDeleted(true);
        categoryRepository.save(category);

        log.info("Category deleted: {}", categoryId);
    }

    @Override
    public MenuItemResponse createMenuItem(Long restaurantId, MenuItemCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı"));

        MenuItem item = mapper.toMenuItem(request);
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setIsAvailable(true);
        item.setIsPopular(false);

        MenuItem saved = menuItemRepository.save(item);
        log.info("Menu item created: {} for restaurant: {}", saved.getId(), restaurantId);

        return mapper.toMenuItemResponse(saved);
    }

    @Override
    public MenuItemResponse updateMenuItem(Long itemId, MenuItemUpdateRequest request) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menü öğesi bulunamadı"));

        if (request.getName() != null) item.setName(request.getName());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getPreparationTime() != null) item.setPreparationTime(request.getPreparationTime());
        if (request.getCalories() != null) item.setCalories(request.getCalories());
        if (request.getIngredients() != null) item.setIngredients(request.getIngredients());
        if (request.getAllergenInfo() != null) item.setAllergenInfo(request.getAllergenInfo());
        if (request.getImageUrl() != null) item.setImageUrl(request.getImageUrl());
        if (request.getIsAvailable() != null) item.setIsAvailable(request.getIsAvailable());
        if (request.getIsPopular() != null) item.setIsPopular(request.getIsPopular());
        if (request.getIsVegetarian() != null) item.setIsVegetarian(request.getIsVegetarian());
        if (request.getIsVegan() != null) item.setIsVegan(request.getIsVegan());
        if (request.getIsGlutenFree() != null) item.setIsGlutenFree(request.getIsGlutenFree());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı"));
            item.setCategory(category);
        }

        MenuItem updated = menuItemRepository.save(item);
        return mapper.toMenuItemResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getRestaurantMenu(Long restaurantId) {
        List<MenuItem> items = menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);

        return items.stream()
                .map(mapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getCategoryItems(Long categoryId) {
        List<MenuItem> items = menuItemRepository.findByCategoryIdAndIsAvailableTrue(categoryId);

        return items.stream()
                .map(mapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getPopularItems(Long restaurantId) {
        List<MenuItem> items = menuItemRepository.findPopularItems(restaurantId);

        return items.stream()
                .map(mapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItem(Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menü öğesi bulunamadı"));

        return mapper.toMenuItemResponse(item);
    }

    @Override
    public void deleteMenuItem(Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menü öğesi bulunamadı"));

        item.setIsAvailable(false);
        item.setIsDeleted(true);
        menuItemRepository.save(item);

        log.info("Menu item deleted: {}", itemId);
    }

    @Override
    public void toggleItemAvailability(Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menü öğesi bulunamadı"));

        item.setIsAvailable(!item.getIsAvailable());
        menuItemRepository.save(item);

        log.info("Menu item availability toggled: {} - Now: {}", itemId, item.getIsAvailable());
    }
}