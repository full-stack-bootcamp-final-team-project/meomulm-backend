package com.meomulm.favorite.model.dto;

import com.meomulm.accommodation.model.dto.AccommodationImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectFavorite {
    private int favoriteId;
    private int userId;
    private int accommodationId;
    private int createdAt;

    private String accommodationName;
    private String accommodationAddress;
    private String accommodationImageUrl;
    /*
    // 숙소 이미지 리스트
    private List<AccommodationImage> accommodationImage;*/

}
