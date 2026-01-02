package com.meomulm.user.model.mapper;


import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.user.model.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 회원정보 조회 메서드
     * @param userId JWT 토큰에서 추출한 userId
     * @return 회원정보
     */
    User selectUserInfoById(int userId);

    /**
     * 회원정보 수정 메서드
     * @param user
     */
    void updateUserInfo(User user);

    /**
     * 회원 리뷰 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return 숙소 정보를 포함한 리뷰 리스트
     */
    List<MyReview> selectUserReviewsById(int userId);

    /**
     * 회원 예약 내역 조회
     * @param userId JWT 토큰에서 추출한 userId
     * @return
     */
    List<Reservation> selectUserReservationById(int userId);

    /**
     * 프로필 사진 수정
     * @param user 프로필 사진이 담긴 user 객체
     */
    void updateProfileImage(User user);

}
