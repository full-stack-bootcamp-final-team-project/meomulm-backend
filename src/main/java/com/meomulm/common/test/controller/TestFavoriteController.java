package com.meomulm.common.test.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.favorite.model.dto.SelectFavorite;
import com.meomulm.favorite.model.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test/favorite")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "찜 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class TestFavoriteController {

    private final FavoriteService favoriteService;
    private final AuthUtil authUtil;

    @Operation(
            summary = "찜 목록 조회",
            description = "로그인한 사용자의 찜 목록을 조회합니다.\n\n" +
                    "이 API는 인증이 필요합니다. 먼저 /api/test/token에서 토큰을 발급받아 Authorize에 등록하세요."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SelectFavorite.class),
                            examples = @ExampleObject(
                                    value = """
                                    [
                                      {
                                        "favoriteId": 1,
                                        "userId": 1,
                                        "accommodationId": 10,
                                        "createdAt": "2024-01-15T10:30:00",
                                        "accommodationName": "서울 호텔",
                                        "accommodationAddress": "서울시 강남구",
                                        "accommodationImageUrl": "https://example.com/image.jpg"
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찜 목록이 없습니다"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰"
            )
    })
    @GetMapping
    public ResponseEntity<List<SelectFavorite>> getFavorites(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        List<SelectFavorite> favorites = favoriteService.getFavorites(currentUserId);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "찜 추가",
            description = "특정 숙소를 찜 목록에 추가합니다.\n\n" +
                    "이 API는 인증이 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추가 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 이미 찜한 숙소이거나 존재하지 않는 숙소"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰"
            )
    })
    @PostMapping("/{accommodationId}")
    public ResponseEntity<Void> postFavorite(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "숙소 ID", required = true, example = "10")
            @PathVariable int accommodationId) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        favoriteService.postFavorite(currentUserId, accommodationId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "찜 삭제",
            description = "찜 목록에서 특정 찜을 삭제합니다.\n\n" +
                    "이 API는 인증이 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찜을 찾을 수 없습니다"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 다른 사용자의 찜을 삭제할 수 없습니다"
            )
    })
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "찜 ID", required = true, example = "1")
            @PathVariable int favoriteId) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        favoriteService.deleteFavorite(currentUserId, favoriteId);
        return ResponseEntity.ok().build();
    }
}