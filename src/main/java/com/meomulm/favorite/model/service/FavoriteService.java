package com.meomulm.favorite.model.service;

import com.meomulm.favorite.model.dto.SelectFavorite;

import java.util.List;

public interface FavoriteService {
    /**
     * 사용자 찜 목록 가져오기
     * @param userId 유저 ID
     * @return 찜 목록 조회 DTO 리스트
     */
    List<SelectFavorite> getFavorites(int userId);

    /**
     * 사용자 찜 추가하기
     * @param userId 유저 ID
     * @param accommodationId 숙소 ID
     */
    void postFavorite(int userId, int accommodationId);

    /**
     * 사용자 찜 삭제하기
     * @param userId 유저 ID
     * @param favoriteId 찜 ID
     */
    void deleteFavorite(int userId, int favoriteId);
}
