package com.meomulm.user.model.mapper;


import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.user.model.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    // ==========================================
    //                  My Page
    // ==========================================
    /**
     * 회원정보 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return 회원정보
     */
    User selectUserInfoById(int userId);

    /**
     * 회원정보 수정
     * @param user 수정하려는 회원 정보
     */
    void updateUserInfo(User user);

    /**
     * 회원 예약 내역 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return
     */
    List<Reservation> selectUserReservationById(int userId);

    /**
     * 프로필 사진 수정
     * @param userProfileImage 회원 프로필 이미지 경로
     */
    void updateProfileImage(String userProfileImage, int userId);

    /**
     * 현재 비밀번호 확인
     * @param userId JWT 토큰에서 추출한 userId
     * @return 해당 회원의 비밀번호
     */
    String selectCurrentPassword(int userId);

    /**
     * 비밀번호 수정 (마이페이지)
     * @param user 수정하려는 회원 비밀번호를 포함한 객체
     */
    void updateMyPagePassword(int userId, String userPassword);

    // ==========================================
    //               Signup / Login
    // ==========================================
    /**
     * 회원가입
     * @param user 회원 정보
     */
    void insertUser(User user);

    /**
     * 로그인
     * @param userEmail 로그인하려는 회원 이메일
     * @return 로그인 성공 여부
     */
    User selectUserLogin(String userEmail);

    /**
     * 아이디 찾기
     * @param userName 아이디를 찾으려는 회원 이름
     * @param userPhone 아이디를 찾으려는 회원 전화번호
     * @return 해당 회원의 이메일
     */
    String selectUserFindId(String userName, String userPhone);

    /**
     * 비밀번호 찾기
     * @param userEmail 회원 이메일 정보
     * @param userBirth 회원 생일 정보
     * @return 회원 ID 정보
     */
    Integer selectUserFindPassword(String userEmail, String userBirth);

    /**
     * 비밀번호 번경 (로그인페이지)
     * @param userId 조회되는 회원 ID
     * @param userPassword 수정하는 회원 비밀번호
     * @return 변경 성공 여부
     */
    int updateUserPassword(Long userId,String userPassword);

    /**
     * 이메일 조회
     * @param userEmail 회원 이메일
     * @return
     */
    User selectUserByUserEmail(String userEmail);

    /**
     * 전화번호 조회
     * @param userPhone 회원 전화번호
     * @return
     */
    String selectUserByUserPhone(String userPhone);
}
