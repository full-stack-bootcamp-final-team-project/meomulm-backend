package com.meomulm.user.model.service;

import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.user.model.dto.User;

import java.util.List;

public interface UserService {
    // ==========================================
    //                  My Page
    // ==========================================
    /**
     * 회원정보 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return
     */
    User getUserInfoById(int userId);

    /**
     * 회원정보 수정
     * @param user
     * @return
     */
    boolean putUserInfo(User user);

    /**
     * 회원 예약 내역 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return 회원 예약 리스트
     */
    List<Reservation> selectUserReservationById(int userId);

    /**
     * 프로필 사진 수정
     * @param user userProfileImage를 포함한 user 객체
     */
    void updateProfileImage(User user);

    /**
     * 현재 비밀번호 확인
     * @param userId JWT 토큰에서 추출한 userId
     * @return 해당 회원의 비밀번호
     */
    String getCurrentPassword(int userId);

    /**
     * 비밀번호 수정 (마이페이지)
     * @param user 수정하려는 회원 비밀번호를 포함한 객체
     */
    void putMyPagePassword(User user);

    // ==========================================
    //               Signup / Login
    // ==========================================

}
