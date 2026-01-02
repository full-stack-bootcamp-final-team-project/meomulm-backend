package com.meomulm.favorite.controller;

import com.meomulm.common.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<SelectFavorite>> getFavorites(@RequestHeader("Authorization") String authHeader){

        try {
            String token = authHeader.substring(7);
            int userId = jwtUtil.getUserIdFromToken(token);
            List<SelectFavorite> favorites = favoriteService.getFavorites(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{accommodationId}")
    public ResponseEntity<String> addFavorite(@PathVariable int accommodationId,
                                           @RequestHeader("Authorization") String authHeader){
        try {
            String token = authHeader.substring(7);
            int userId = jwtUtil.getUserIdFromToken(token);

            favoriteService.addFavorite(userId, accommodationId);
            return ResponseEntity.ok("즐겨찾기 추가 성공");
        } catch (Exception e)  {
            log.error("즐겨찾기 추가 실패 : {}", e.getMessage());
            return ResponseEntity.badRequest().body("즐겨찾기 추가 중 오류 발생");
        }
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable int favoriteId,
                                              @RequestHeader("Authorization") String authHeader){
        try {
            String token = authHeader.substring(7);
            int userId = jwtUtil.getUserIdFromToken(token);

            favoriteService.deleteFavorite(userId, favoriteId);
            return ResponseEntity.ok("즐겨찾기 삭제 성공");
        } catch (Exception e)  {
            log.error("즐겨찾기 삭제 실패 : {}", e.getMessage());
            return ResponseEntity.badRequest().body("즐겨찾기 삭제 중 오류 발생");
        }
    }
}
