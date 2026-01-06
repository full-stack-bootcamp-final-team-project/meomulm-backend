package com.meomulm.favorite.model.service;

import com.meomulm.favorite.model.dto.Favorite;
import com.meomulm.favorite.model.dto.SelectFavorite;
import com.meomulm.favorite.model.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public List<SelectFavorite> getFavorites(int userId){
        log.info("서비스에서 가져온 데이터들 : {}", favoriteMapper.selectFavorites(userId));
        return favoriteMapper.selectFavorites(userId);
    }



    @Override
    public void postFavorite(int userId, int accommodationId){
        try {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setAccommodationId(accommodationId);
            favoriteMapper.insertFavorite(favorite);
        } catch (Exception e) {
            log.error("즐겨찾기 추가 중 오류 발생 : {}", e.getMessage());
        }
    }

    @Override
    public void deleteFavorite(int userId, int favoriteId){
        favoriteMapper.deleteFavorite(userId, favoriteId);
    }
}
