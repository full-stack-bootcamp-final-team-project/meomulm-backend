package com.meomulm.favorite.model.service;

import com.meomulm.favorite.model.dto.SelectFavorite;

import java.util.List;

public interface FavoriteService {
    List<SelectFavorite> getFavorites(int userId); // 숙소 Id 하나만 가져와서 String
    void postFavorite(int userId, int accommodationId);
    void deleteFavorite(int userId, int favoriteId);
}
