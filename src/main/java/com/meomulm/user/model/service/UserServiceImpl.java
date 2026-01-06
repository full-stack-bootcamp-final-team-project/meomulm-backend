package com.meomulm.user.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.common.util.FileUploadService;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.LoginRequest;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadService fileUploadService;

    // ==========================================
    //                  My Page
    // ==========================================
    // 회원정보 조회
    @Override
    public User getUserInfoById(int userId) {
        log.info("💡 회원정보 조회 시작. userId: {}", userId);
        User user = userMapper.selectUserInfoById(userId);

        if (user == null) {
            log.warn("⚠️ 조회 결과 - 사용자 없음. userId: {}", userId);
            throw new BadRequestException("사용자를 찾을 수 없습니다.");
        }

        log.info("✅ 회원정보 조회 성공. userId: {}", user.getUserId());
        return user;
    }

    // 회원정보 수정
    @Override
    public void putUserInfo(User user, int currentUserId) {
        log.info("💡 회원정보 수정 시작. userId: {}", currentUserId);
        userMapper.updateUserInfo(user.getUserName(), user.getUserPhone(), currentUserId);

        log.info("✅ 회원정보 수정 완료. userId: {}", currentUserId);
    }

    // 회원 예약 내역 조회
    @Override
    public List<Reservation> selectUserReservationById(int userId) {
        log.info("💡 예약내역 조회 시작. userId: {}", userId);
        List<Reservation> reservations = userMapper.selectUserReservationById(userId);

        if (reservations == null) {
            log.warn("⚠️ 조회 결과 - 예약내역 없음. userId: {}", userId);
            throw new BadRequestException("예약이 존재하지 않습니다.");
        }

        log.info("✅ 예약내역 조회 성공. userId: {}", userId);
        return reservations;
    }

    // 프로필 사진 수정
    @Override
    public void updateProfileImage(MultipartFile userProfileImage, int userId) {
        try {
            log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
            if (userProfileImage == null || userProfileImage.isEmpty()) {
                log.warn("⚠️ 프로필 이미지가 존재하지 않음. userId: {}", userId);
                throw new NotFoundException("프로필 사진이 존재하지 않습니다.");
            }

            // MultipartFile -> String
            String saveImagePath = fileUploadService.uploadProfileImage(userProfileImage);

            log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
            userMapper.updateProfileImage(saveImagePath, userId);

            log.info("✅ 프로필 사진 수정 성공. userId: {}, userProfileImage: {}", userId, userProfileImage);
        } catch (IOException e) {
            log.error("❌ 프로필 사진 수정 실패. userId: {}, userProfileImage: {}", userId, userProfileImage, e);
        }
    }

    // 현재 비밀번호 확인
    @Override
    public void getCurrentPassword(int userId, String inputPassword) {
        log.info("💡 현재 비밀번호 확인 시작. userId: {}", userId);
        if (inputPassword == null || inputPassword.isEmpty()) {
            log.warn("⚠️ 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("입력한 비밀번호가 존재하지 않습니다.");
        }

        String currentPassword = userMapper.selectCurrentPassword(userId);

        if (!bCryptPasswordEncoder.matches(inputPassword, currentPassword)) {
            log.warn("⚠️ 비밀번호 불일치. inputPassword: {}, currentPassword: {}", inputPassword, currentPassword);
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        log.info("✅ 현재 비밀번호 조회 성공. userId: {}", userId);
    }

    // 비밀번호 수정
    @Override
    public void putMyPagePassword(int userId, String newPassword) {
        log.info("💡 비밀번호 수정 시작. userId: {}", userId);
        if (newPassword == null || newPassword.isEmpty()) {
            log.warn("⚠️ 새 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("입력한 새 비밀번호가 존재하지 않습니다.");
        }

        userMapper.updateMyPagePassword(userId, bCryptPasswordEncoder.encode(newPassword));
        log.info("✅ 비밀번호 수정 성공. userId: {}", userId);
    }

    // ==========================================
    //               Signup / Login
    // ==========================================
    // 회원가입
    @Transactional
    @Override
    public void signup(User user) {
        User existingEmail = userMapper.selectUserByUserEmail(user.getUserEmail());

        if (existingEmail != null) {
            log.warn("❌ 이미 존재하는 이메일 : {}", existingEmail);
            throw new NotFoundException("이미 존재하는 이메일입니다.");
        }

        User existingPhone = userMapper.selectUserByUserPhone(user.getUserPhone());
        if (existingPhone != null) {
            log.warn("❌ 이미 존재하는 전화번호 : {}", existingPhone);
            throw new NotFoundException("이미 존재하는 전화번호입니다.");
        }

        String encodePw = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodePw);
        userMapper.insertUser(user);
        log.info("✅ 회원가입 완료 - 이메일 {}, 사용자명 : {}", user.getUserEmail(), user.getUserName());
    }

    // 로그인
    @Override
    public User login(String userEmail, String userPassword) {
        User user = userMapper.selectUserLogin(userEmail);

        if (bCryptPasswordEncoder.matches(userPassword, user.getUserPassword())) {
            log.info("✅ 로그인 성공 - 이메일 : {}", userEmail);
            return user;
        }

        throw new NotFoundException("로그인 정보 없음");
    }

    // 아이디 찾기
    @Override
    public String getUserFindId(String userName, String userPhone) {
        String userEmail = userMapper.selectUserFindId(userName, userPhone);

        if (userEmail != null) {
            log.info("✅ 아이디 찾기 성공 : {}", userEmail);
            return userEmail;
        }
        throw new NotFoundException("이메일 정보 없음");
    }

    // 비밀번호 찾기
    @Override
    public int getUserFindPassword(String userEmail, String userBirth) {
        int userId = userMapper.selectUserFindPassword(userEmail, userBirth);

        if (userId != 0) {
            throw new NotFoundException("유저 정보 없음");
        }

        log.info("✅ 유저 정보 확인 성공 이메일 : {}, 생년: {}", userEmail, userBirth);
        return userId;
    }

    // 비밀번호 변경
    @Transactional
    @Override
    public int patchUserPassword(int userId, String newPassword) {
        log.info("💡 비밀번호 수정 시작. userId: {}", userId);

        if (newPassword == null || newPassword.isEmpty()) {
            log.warn("⚠️ 새 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new BadRequestException("새 비밀번호가 존재하지 않습니다.");
        }

        log.info("✅ 비밀번호 수정 성공. userId: {}", userId);
        return userMapper.updateUserPassword(userId, bCryptPasswordEncoder.encode(newPassword));
    }
}