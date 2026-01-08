package com.meomulm.favorite.model.service;

import com.meomulm.common.exception.NotFoundException;
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
            List<SelectFavorite> favorites = favoriteMapper.selectFavorites(userId);
            log.info("서비스에서 가져온 데이터들 : {}", favorites);
        if (favorites == null || favorites.isEmpty()) {
            log.info("서비스에서 가져온 데이터들 : {}", favorites);
            throw new NotFoundException("사용자의 즐겨찾기 목록이 없습니다.");
        }
            return favorites;
    }



    @Override
    public void postFavorite(int userId, int accommodationId){
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setAccommodationId(accommodationId);
            favoriteMapper.insertFavorite(favorite);


    }

    @Override
    public void deleteFavorite(int userId, int favoriteId){
            favoriteMapper.deleteFavorite(userId, favoriteId);

    }
}
