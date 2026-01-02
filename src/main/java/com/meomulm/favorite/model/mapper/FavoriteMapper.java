package com.meomulm.favorite.model.mapper;


import com.meomulm.favorite.model.dto.SelectFavorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    List<SelectFavorite> getFavorites(int userId); // 숙소 Id 하나만 가져와서 String
    void addFavorite(int userId, int accommodationId);
    void deleteFavorite(int userId, int favoriteId);
}
