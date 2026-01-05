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
     * @param user 회원가입할 회원 정보
     */
    @PostMapping("/signup")
    public void signupUser(@RequestBody User user) {
        userService.signupUser(user);
    }

    /**
     * 로그인
     * @param request 로그인할 유저의 정보
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest request) {
        try{
            User user = userService.userLogin(request.getUserEmail(), request.getUserPassword());

            if (user == null) {
                return ResponseEntity.status(401).body(null);
            }

            String token = jwtUtil.generateToken(user.getUserId(), user.getUserEmail());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);

            log.info("✅ 로그인 성공 - 이메일 : {}", user.getUserEmail());
            return ResponseEntity.ok(loginResponse);

        }catch(Exception e) {
            log.error("❌ 로그인 실패 - 이메일 : {}", e.getMessage());
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/findId")
    public String getUserFindId(@RequestParam String userName,@RequestParam String userPhone){
        return userService.getUserFindId(userName, userPhone);
    }

    @GetMapping("/checkPassword")
    public Integer getUserFindPassword(@RequestParam String userEmail,@RequestParam String userBirth) {
        return userService.getUserFindPassword(userEmail, userBirth);
    }

    @PatchMapping()
    public void patchUserPassword(Long userId, String newPassword){

    }

}
