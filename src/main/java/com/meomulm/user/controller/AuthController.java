package com.meomulm.user.controller;

import com.meomulm.common.util.JwtUtil;
import com.meomulm.user.model.dto.LoginRequest;
import com.meomulm.user.model.dto.LoginResponse;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {
    // ==========================================
    //               Signup / Login
    // ==========================================
    private final UserServiceImpl userService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     * @param user 회원 정보가 들어있는 객체
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody User user) {
        userService.signup(user);
        return ResponseEntity.ok().build();
    }

    /**
     * 로그인
     * @param request 로그인 정보(회원 이메일, 회원 비밀번호)
     * @return  토큰?
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
     * @param user 회원 정보 (회원 이름, 회원 전화번호)
     * @return  회원 이메일
     */
    @GetMapping("/findId")
    public ResponseEntity<String> getUserFindId(@RequestBody User user){
        String userEmail = userService.getUserFindId(user.getUserName(), user.getUserPhone());
        return ResponseEntity.ok(userEmail);
    }

    /**
     * 비밀번호 찾기
     * @param user 회원 정보 (회원 이메일, 회원 생년)
     * @return
     */
    @GetMapping("/checkPassword")
    public ResponseEntity<Integer> getUserFindPassword(@RequestBody User user) {
        int userId = userService.getUserFindPassword(user.getUserEmail(), user.getUserBirth());
        return ResponseEntity.ok(userId);
    }

    /**
     * 비밀번호 변경 (로그인페이지)
     * @param user 회원 id, 새 비밀번호가 담겨있는 객체
     * @return
     */
    @PatchMapping("/changePassword")
    public ResponseEntity<Void> patchUserPassword(@RequestBody User user){
        userService.patchUserPassword(user.getUserId(), user.getUserPassword());
        return ResponseEntity.ok().build();
    }
}