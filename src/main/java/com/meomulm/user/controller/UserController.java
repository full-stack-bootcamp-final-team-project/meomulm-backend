package com.meomulm.user.controller;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.util.AuthUtil;
import com.meomulm.common.util.JwtUtil;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    // ==========================================
    //                  My Page
    // ==========================================
    private final AuthUtil authUtil;
    private final UserService userService;

    /**
     * 회원정보 조회
     * @param authHeader JWT 토큰 헤더
     * @return User 객체 + 상태코드 200 / 예외처리: GlobalExceptionHandler
     */
    @GetMapping
    public ResponseEntity<User> getUserInfoById(
            @RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        User user = userService.getUserInfoById(currentUserId);

        return ResponseEntity.ok(user);
    }

    /**
     * 회원정보 수정
     * @param authHeader JWT 토큰 헤더
     * @param user 프론트에서 body로 받아온 User 데이터
     * @return 상태코드 200 / 예외처리: GlobalExceptionHandler
     */
    @PutMapping("/userInfo")
    public ResponseEntity<Void> putUserInfo(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody User user){
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.putUserInfo(user);

        return ResponseEntity.ok().build();
    }

    /**
     * 회원 예약 내역 조회
     * @param authHeader JWT 토큰 헤더
     * @return 예약내역 리스트 + 상태코드 200 / 예외처리: GlobalExceptionHandler
     */
    @GetMapping("/reservation")
    public ResponseEntity<List<Reservation>> selectUserReservationById(@RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        List<Reservation> reservations = userService.selectUserReservationById(currentUserId);

        return ResponseEntity.ok(reservations);
    }

    /**
     * 프로필 사진 수정
     * @param authHeader JWT 토큰 헤더
     * @param userProfileImage 새로 저장할 프로필 이미지
     * @return
     */
    @PatchMapping("/profileImage")
    public ResponseEntity<Void> updateProfileImage(@RequestHeader("Authorization") String authHeader,
                                                   @RequestPart MultipartFile userProfileImage) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.updateProfileImage(userProfileImage, currentUserId);

        return ResponseEntity.ok().build();
    }

    /**
     * 현재 비밀번호 확인
     * @param authHeader JWT 토큰 헤더
     * @param currentPassword 입력된 현재 비밀번호
     * @return 상태코드 200 / 예외처리: GlobalExceptionHandler
     */
    @GetMapping("/currentPassword")
    public ResponseEntity<Void> getCurrentPassword(@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody String currentPassword) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.getCurrentPassword(currentUserId, currentPassword);

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 수정
     * @param authHeader JWT 토큰 헤더
     * @param newPassword 변경하려는 새 비밀번호
     * @return 상태코드 200 / 예외처리: GlobalExceptionHandler
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> putMyPagePassword(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody String newPassword) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.putMyPagePassword(currentUserId, newPassword);

        return ResponseEntity.ok().build();
    }
}