package com.meomulm.favorite.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    private int favoriteId;
    private int userId;
    private int accommodationId;
    private int createdAt;
}


