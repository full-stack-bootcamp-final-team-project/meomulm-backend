package com.meomulm.favorite.model.service;

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
        return favoriteMapper.selectFavorites(userId);
    }

    @Override
    public void postFavorite(int userId, int accommodationId){
        favoriteMapper.insertFavorite(userId, accommodationId);
    }

    @Override
    public void deleteFavorite(int userId, int favoriteId){
        favoriteMapper.deleteFavorite(userId, favoriteId);
    }
}
