package com.eatlens.app.controller;


import com.eatlens.app.dto.categorydto.CategoryCreateRequest;
import com.eatlens.app.dto.categorydto.CategoryResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemCreateRequest;
import com.eatlens.app.dto.menuitemdto.MenuItemResponse;
import com.eatlens.app.dto.menuitemdto.MenuItemUpdateRequest;
import com.eatlens.app.dto.userdto.BaseResponse;
import com.eatlens.app.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // Category Endpoints
    @PostMapping("/restaurants/{restaurantId}/categories")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<CategoryResponse> createCategory(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = menuService.createCategory(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = menuService.updateCategory(categoryId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @PathVariable Long restaurantId) {
        List<CategoryResponse> response = menuService.getRestaurantCategories(restaurantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<BaseResponse> deleteCategory(@PathVariable Long categoryId) {
        menuService.deleteCategory(categoryId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Kategori başarıyla silindi");
        return ResponseEntity.ok(response);
    }

    // Menu Item Endpoints
    @PostMapping("/restaurants/{restaurantId}/items")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemCreateRequest request) {
        MenuItemResponse response = menuService.createMenuItem(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemUpdateRequest request) {
        MenuItemResponse response = menuService.updateMenuItem(itemId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/items")
    public ResponseEntity<List<MenuItemResponse>> getRestaurantMenu(
            @PathVariable Long restaurantId) {
        List<MenuItemResponse> response = menuService.getRestaurantMenu(restaurantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{categoryId}/items")
    public ResponseEntity<List<MenuItemResponse>> getCategoryItems(
            @PathVariable Long categoryId) {
        List<MenuItemResponse> response = menuService.getCategoryItems(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/{restaurantId}/popular")
    public ResponseEntity<List<MenuItemResponse>> getPopularItems(
            @PathVariable Long restaurantId) {
        List<MenuItemResponse> response = menuService.getPopularItems(restaurantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable Long itemId) {
        MenuItemResponse response = menuService.getMenuItem(itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<BaseResponse> deleteMenuItem(@PathVariable Long itemId) {
        menuService.deleteMenuItem(itemId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Menü öğesi başarıyla silindi");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{itemId}/toggle-availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<BaseResponse> toggleItemAvailability(@PathVariable Long itemId) {
        menuService.toggleItemAvailability(itemId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Ürün durumu güncellendi");
        return ResponseEntity.ok(response);
    }
}