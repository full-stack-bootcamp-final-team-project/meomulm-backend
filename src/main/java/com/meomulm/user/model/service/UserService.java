package com.meomulm.user.model.service;

import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.user.model.dto.LoginRequest;
import com.meomulm.user.model.dto.LoginResponse;
import com.meomulm.user.model.dto.User;
import org.springframework.web.multipart.MultipartFile;

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
    void putUserInfo(User user, int currentUserId);

    /**
     * 회원 예약 내역 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return 회원 예약 리스트
     */
    List<Reservation> selectUserReservationById(int userId);

    /**
     * 프로필 사진 수정
     * @param userProfileImage 사용자 프로필 이미지 경로
     */
    void updateProfileImage(MultipartFile userProfileImage, int userId);

    /**
     * 현재 비밀번호 확인
     * @param userId JWT 토큰에서 추출한 userId
     * @return 해당 회원의 비밀번호
     */
    void getCurrentPassword(int userId, String inputPassword);

    /**
     * 비밀번호 수정 (마이페이지)
     * @param userId JWT 토큰에서 추출한 userId
     * @param newPassword 변경하려는 비밀번호
     */
    void putMyPagePassword(int userId, String newPassword);

    // ==========================================
    //               Signup / Login
    // ==========================================

    /**
     * 회원가입
     * @param user 회원 정보
     */
    void signup(User user);

    /**
     * 로그인
     * @param userEmail     로그인 할 회원 이메일
     * @param userPassword  로그인 할 회원 비밀번호
     * @return  로그인 성공 유무
     */
    User login(String userEmail, String userPassword);

    /**
     * 아이디 찾기
     * @param userName  회원 이름
     * @param userPhone 회원 전화번호
     * @return  회원 이메일
     */
    String getUserFindId(String userName, String userPhone);

    /**
     * 비밀번호 찾기
     * @param userEmail 회원 이메일
     * @param userBirth 회원 생년
     * @return  회원 유무 확인
     */
    int getUserFindPassword(String userEmail, String userBirth);

    /**
     * 비밀번호 변경 (로그인 페이지)
     * @param userId        회원 id
     * @param newPassword   새 비밀번호
     * @return  성공 여부
     */
    void patchUserPassword(int userId, String newPassword);

}