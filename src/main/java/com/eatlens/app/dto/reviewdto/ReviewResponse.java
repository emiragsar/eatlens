package com.eatlens.app.dto.reviewdto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.eatlens.app.dto.userdto.UserResponse;

@Data
public class ReviewResponse {
    private Long id;
    private UserResponse customer;
    private Integer rating;
    private String comment;
    private Integer foodRating;
    private Integer serviceRating;
    private Integer ambianceRating;
    private Integer priceRating;
    private Boolean isVerifiedPurchase;
    private Integer helpfulCount;
    private List<String> imageUrls;
    private String ownerResponse;
    private LocalDateTime createdAt;
}
