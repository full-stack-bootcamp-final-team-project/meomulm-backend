package com.meomulm.user.controller;

import com.meomulm.common.util.JwtUtil;
import com.meomulm.user.model.dto.LoginRequest;
import com.meomulm.user.model.dto.LoginResponse;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.service.KakaoServiceImpl;
import com.meomulm.user.model.service.NaverServiceImpl;
import com.meomulm.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {
    // ==========================================
    //               Signup / Login
    // ==========================================
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final KakaoServiceImpl kakaoService;
    private final NaverServiceImpl naverService;

    /**
     * 회원가입
     * @param user 유저 객체
     * @return 상태코드 200
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody User user) {
        userService.signup(user);
        return ResponseEntity.ok().build();
    }

    /**
     * 이메일 중복 확인
     * @param userEmail 유저가 작성한 이메일
     * @return 상태코드 200
     */
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String userEmail) {
        return ResponseEntity.ok(!userService.existsByUserEmail(userEmail));
    }


    /**
     * 전화번호 중복 확인
     * @param userPhone 유저가 작성한 전화번호
     * @return 상태코드 200
     */
    @GetMapping("/checkPhone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam("phone") String userPhone) {
        return ResponseEntity.ok(!userService.existsByUserPhone(userPhone));
    }


    /**
     * 로그인
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO + 상태코드 200
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUserEmail(), request.getUserPassword());

        String token = jwtUtil.generateToken(user.getUserId(), user.getUserEmail());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);

        log.info("✅ 토큰 생성 완료 - 이메일 : {}", user.getUserEmail());
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 아이디 찾기
     * @param userName 유저 userName
     * @param userPhone 유저 userPhone
     * @return  회원 이메일 + 상태코드 200
     */
    @GetMapping("/findId")
    public ResponseEntity<String> getUserFindId(@RequestParam("userName") String userName, @RequestParam("userPhone") String userPhone){
        String userEmail = userService.getUserFindId(userName, userPhone);
        return ResponseEntity.ok(userEmail);
    }

    /**
     * 본인인증 (비밀번호 변경 시)
     * @param userEmail 유저 userEmail
     * @param userBirth 유저 userBirth
     * @return 유저ID + 상태코드 200
     */
    @GetMapping("/checkPassword")
    public ResponseEntity<Integer> getUserFindPassword(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("userBirth") String userBirth) {
        int result = userService.getUserFindPassword(userEmail, userBirth);
        return ResponseEntity.ok(result);
    }

    // ==========================================
    //            이메일 인증 (DB 저장)
    // ==========================================

    /**
     * 이메일 인증코드 전송
     * 기존 세션 저장 → DB 저장으로 변경
     *
     * @param body userEmail을 담은 요청 바디
     * @return 전송 성공 1 / 실패 0
     */
    @PostMapping("/sendEmailCode")
    public ResponseEntity<Integer> sendEmailCode(@RequestBody Map<String, String> body) {
        String userEmail = body.get("userEmail");

        if (userEmail == null || userEmail.isBlank()) {
            return ResponseEntity.badRequest().body(0);
        }

        int result = userService.sendEmailAndSaveAuth(userEmail);
        return ResponseEntity.ok(result);
    }

    /**
     * 인증번호 검증
     * 기존 세션 조회 → DB 조회로 변경
     *
     * @param body userEmail, inputCode를 담은 요청 바디
     * @return 검증 성공 1 / 실패 0
     */
    @PostMapping("/verifyEmailCode")
    public ResponseEntity<Integer> checkAuthKey(@RequestBody Map<String, String> body) {
        String userEmail = body.get("userEmail");
        String inputCode  = body.get("inputCode");

        if (userEmail == null || inputCode == null) {
            return ResponseEntity.badRequest().body(0);
        }

        int result = userService.verifyEmailCode(userEmail, inputCode);
        return ResponseEntity.ok(result);
    }

    /**
     * 비밀번호 변경 (로그인 페이지)
     * @param user 유저 객체 (userEmail, 새 비밀번호)
     * @return 상태코드 200
     */
    @PatchMapping("/changePassword")
    public ResponseEntity<Void> patchUserPassword(@RequestBody User user) {
        userService.patchUserPassword(user.getUserEmail(), user.getUserPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * 카카오 로그인
     * @param data 카카오 유저 정보
     * @return 에러코드 / 로그인 응답 DTO + 상태코드 200
     */
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> data) {
        String accessToken = data.get("accessToken");
        log.info("💡 카카오 로그인 요청 - accessToken 앞 20자: {}",
                accessToken != null && accessToken.length() > 20 ? accessToken.substring(0, 20) : "null");

        if(accessToken == null || accessToken.isEmpty()){
            log.error("❌ 카카오 accessToken이 null 이거나 비어있습니다.");
            return ResponseEntity.status(400).body(Map.of("error", "액세스 토큰이 없습니다."));
        }

        User kakaoUser = kakaoService.getKakaoUserInfo(accessToken);
        if(kakaoUser == null) {
            log.error("❌ 카카오 사용자 정보 조회 실패");
            return ResponseEntity.status(400).body(Map.of("error", "카카오 유저 정보 조회 실패"));
        }

        log.info("✅ 카카오 사용자 정보 조회 성공 - email: {}", kakaoUser.getUserEmail());

        User existUser = userService.getUserByUserEmail(kakaoUser.getUserEmail());

        if(existUser != null){
            String token = jwtUtil.generateToken(existUser.getUserId(), existUser.getUserEmail());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);

            log.info("✅ 카카오 로그인 성공: {}", existUser.getUserEmail());
            return ResponseEntity.ok(loginResponse);

        } else {
            // 미가입 회원인 경우
            Map<String, Object> response = new HashMap<>();
            response.put("message", "need_signup");
            response.put("kakaoUser", Map.of(
                    "userEmail", kakaoUser.getUserEmail(),
                    "userName", kakaoUser.getUserName() != null ? kakaoUser.getUserName() : "",
                    "userProfileImage", kakaoUser.getUserProfileImage() != null ? kakaoUser.getUserProfileImage() : ""
            ));
            log.info("⚠️ 카카오 로그인 - 미가입 회원: {}", kakaoUser.getUserEmail());
            return ResponseEntity.status(202).body(response);
        }
    }

    @PostMapping("/naver")
    public ResponseEntity<?> naverLogin(@RequestBody Map<String, String> data) {
        String accessToken = data.get("accessToken");

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "액세스 토큰이 없습니다."));
        }

        User naverUser = naverService.getNaverUserInfo(accessToken);
        if (naverUser == null) {
            return ResponseEntity.status(400).body(Map.of("error", "네이버 유저 정보 조회 실패"));
        }

        User existUser = userService.getUserByUserEmail(naverUser.getUserEmail());

        if (existUser != null) {
            String token = jwtUtil.generateToken(existUser.getUserId(), existUser.getUserEmail());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            return ResponseEntity.ok(loginResponse);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "need_signup");
            response.put("naverUser", Map.of(
                    "userEmail", naverUser.getUserEmail(),
                    "userName", naverUser.getUserName() != null ? naverUser.getUserName() : ""
            ));
            return ResponseEntity.status(202).body(response);
        }
    }
}