package com.eatlens.app.service;




import com.eatlens.app.dto.categorydto.CategoryCreateRequest;
import com.eatlens.app.dto.categorydto.CategoryResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemCreateRequest;
import com.eatlens.app.dto.menuitemdto.MenuItemResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemUpdateRequest;

import java.util.List;

public interface MenuService {
    // Category operations
    CategoryResponse createCategory(Long restaurantId, CategoryCreateRequest request);
    CategoryResponse updateCategory(Long categoryId, CategoryCreateRequest request);
    List<CategoryResponse> getRestaurantCategories(Long restaurantId);
    void deleteCategory(Long categoryId);

    // Menu Item operations
    MenuItemResponse createMenuItem(Long restaurantId, MenuItemCreateRequest request);
    MenuItemResponse updateMenuItem(Long itemId, MenuItemUpdateRequest request);
    List<MenuItemResponse> getRestaurantMenu(Long restaurantId);
    List<MenuItemResponse> getCategoryItems(Long categoryId);
    List<MenuItemResponse> getPopularItems(Long restaurantId);
    MenuItemResponse getMenuItem(Long itemId);
    void deleteMenuItem(Long itemId);
    void toggleItemAvailability(Long itemId);
}