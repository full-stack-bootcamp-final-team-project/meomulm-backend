package com.meomulm.favorite.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.favorite.model.dto.SelectFavorite;
import com.meomulm.favorite.model.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthUtil authUtil;



    // 사용자 찜 목록 가져오기
     @GetMapping
    public ResponseEntity<List<SelectFavorite>> getFavorites(@RequestHeader("Authorization") String authHeader){


            int currentUserId = authUtil.getCurrentUserId(authHeader);
            List<SelectFavorite> favorites = favoriteService.getFavorites(currentUserId);
            return ResponseEntity.ok(favorites);

    }

    // 사용자 찜 추가하기
    @PostMapping("/{accommodationId}")
    public ResponseEntity<String> postFavorite(@PathVariable int accommodationId,
                                           @RequestHeader("Authorization") String authHeader){

            int currentUserId = authUtil.getCurrentUserId(authHeader);

            favoriteService.postFavorite(currentUserId, accommodationId);
            return ResponseEntity.ok("즐겨찾기 추가 성공");


    }



    // 사용자 찜 삭제하기
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable int favoriteId,
                                              @RequestHeader("Authorization") String authHeader){

            int currentUserId = authUtil.getCurrentUserId(authHeader);

            favoriteService.deleteFavorite(currentUserId, favoriteId);
            return ResponseEntity.ok("즐겨찾기 삭제 성공");

    }


}
