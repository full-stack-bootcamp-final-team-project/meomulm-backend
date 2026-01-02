package com.meomulm.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 유저 ID
    int userId;
    // 유저 이메일
    String userEmail;
    // 유저 비밀번호
    String userPassword;
    // 유저 이름
    String userName;
    // 유저 연락처
    String userPhone;
    // 유저 생년월일
    String userBirth;
    // 유저 프로필 이미지
    String userProfileImage;
    // 생성일자
    String createdAt;
}
