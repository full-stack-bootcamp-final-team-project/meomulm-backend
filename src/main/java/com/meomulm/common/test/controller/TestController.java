package com.meomulm.common.test.controller;

import com.meomulm.common.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test", description = "개발/테스트용 API (운영환경에서는 비활성화 필요)")
public class TestController {

    private final JwtUtil jwtUtil;

    @Operation(
            summary = "테스트 JWT 토큰 발급",
            description = "개발 및 테스트를 위한 JWT 토큰을 발급합니다.\n\n" +
                    "**사용 방법:**\n" +
                    "1. userId와 email을 입력하고 Execute 클릭\n" +
                    "2. Response의 'token' 값을 복사\n" +
                    "3. 우측 상단 Authorize 버튼 클릭\n" +
                    "4. 복사한 token 값만 붙여넣기 (Bearer 제외)\n" +
                    "5. Authorize 후 Close\n" +
                    "6. 이제 다른 API 테스트 가능!"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": true,
                                      "userId": 1,
                                      "email": "test@example.com",
                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwi...",
                                      "authorizationHeader": "Bearer eyJhbGciOiJIUzI1NiJ9...",
                                      "instruction": "위의 'token' 값만 복사해서 Swagger Authorize에 입력하세요"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/token")
    public ResponseEntity<Map<String, Object>> generateTestToken(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam(defaultValue = "1") int userId,
            @Parameter(description = "사용자 이메일", example = "test@example.com")
            @RequestParam(defaultValue = "test@example.com") String email) {

        String token = jwtUtil.generateToken(userId, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("email", email);
        response.put("token", token);
        response.put("authorizationHeader", "Bearer " + token);
        response.put("instruction", "위의 'token' 값만 복사해서 Swagger Authorize에 입력하세요 (Bearer 제외)");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "JWT 토큰 검증",
            description = "발급받은 JWT 토큰이 유효한지 검증하고 포함된 정보를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검증 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "valid": true,
                                      "userId": 1,
                                      "email": "test@example.com",
                                      "message": "유효한 토큰입니다."
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @Parameter(description = "검증할 JWT 토큰", required = true)
            @RequestParam String token) {

        Map<String, Object> response = new HashMap<>();

        boolean isValid = jwtUtil.validateToken(token);
        response.put("valid", isValid);

        if (isValid) {
            try {
                int userId = jwtUtil.getUserIdFromToken(token);
                String email = jwtUtil.getUserEmailFromToken(token);

                response.put("userId", userId);
                response.put("email", email);
                response.put("message", "유효한 토큰입니다.");
            } catch (Exception e) {
                response.put("message", "토큰 파싱 중 오류 발생: " + e.getMessage());
            }
        } else {
            response.put("message", "유효하지 않거나 만료된 토큰입니다.");
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "현재 인증된 사용자 정보 확인",
            description = "Authorization 헤더의 토큰을 통해 현재 인증된 사용자 정보를 확인합니다.\n\n" +
                    "이 API를 테스트하려면 먼저 Authorize 버튼으로 토큰을 등록해야 합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "authenticated": true,
                                      "userId": 1,
                                      "email": "test@example.com",
                                      "message": "인증된 사용자입니다."
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("authenticated", false);
                response.put("message", "Authorization 헤더가 없거나 형식이 잘못되었습니다.");
                return ResponseEntity.status(401).body(response);
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                response.put("authenticated", false);
                response.put("message", "유효하지 않은 토큰입니다.");
                return ResponseEntity.status(401).body(response);
            }

            int userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getUserEmailFromToken(token);

            response.put("authenticated", true);
            response.put("userId", userId);
            response.put("email", email);
            response.put("message", "인증된 사용자입니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("authenticated", false);
            response.put("message", "토큰 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
}