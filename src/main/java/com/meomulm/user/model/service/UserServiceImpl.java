package com.meomulm.user.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // ==========================================
    //                  My Page
    // ==========================================
    // 회원정보 조회
    @Override
    public User getUserInfoById(int userId) {
        try {
            log.info("💡 회원정보 조회 시작. userId: {}", userId);
            User user = userMapper.selectUserInfoById(userId);

            if(user == null) {
                log.warn("⚠️ 조회 결과 - 사용자 없음. userId: {}", userId);
                throw new BadRequestException("사용자를 찾을 수 없습니다.");
            }

            log.info("✅ 회원정보 조회 성공. userId: {}", user.getUserId());
            return user;
        } catch (Exception e) {
            log.error("❌ 회원정보 조회 실패. userId: {}", userId, e);
            throw new RuntimeException("");
        }
    }

    // 회원정보 수정
    @Override
    public void putUserInfo(User user) {
        try {
            log.info("💡 회원정보 수정 시작. userId: {}", user.getUserId());
            userMapper.updateUserInfo(user);

            log.info("✅ 회원정보 수정 완료. userId: {}", user.getUserId());
        } catch (Exception e) {
            log.error("❌ 회원정보 수정 실패. userId: {}", user.getUserId(), e);
            throw new RuntimeException(e);
        }
    }

    // 회원 예약 내역 조회
    @Override
    public List<Reservation> selectUserReservationById(int userId) {
        try {
            log.info("💡 예약내역 조회 시작. userId: {}", userId);
            List<Reservation> reservations = userMapper.selectUserReservationById(userId);

            if(reservations == null) {
                log.warn("⚠️ 조회 결과 - 예약내역 없음. userId: {}", userId);
                throw new BadRequestException("예약이 존재하지 않습니다.");
            }

            log.info("✅ 예약내역 조회 성공. userId: {}", userId);
            return reservations;
        } catch (Exception e) {
            log.error("❌ 예약내역 조회 실패. userId: {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    // 프로필 사진 수정
    @Override
    public void updateProfileImage(String userProfileImage, int userId) {
        try {
            log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
            if(userProfileImage == null || userProfileImage.isEmpty()) {
                log.warn("⚠️ 프로필 이미지가 존재하지 않음. userId: {}", userId);
                throw new BadRequestException("프로필 사진이 존재하지 않습니다.");
            }

            log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
            userMapper.updateProfileImage(userProfileImage, userId);

            log.info("✅ 프로필 사진 수정 성공. userId: {}, userProfileImage: {}", userId, userProfileImage);
        } catch (Exception e) {
            log.error("❌ 프로필 사진 수정 실패. userId: {}, userProfileImage: {}", userId, userProfileImage, e);
            throw new RuntimeException(e);
        }
    }

    // 현재 비밀번호 확인
    @Override
    public void getCurrentPassword(int userId, String inputPassword) {
        try {
            log.info("💡 현재 비밀번호 확인 시작. userId: {}", userId);
            if(inputPassword == null || inputPassword.isEmpty()) {
                log.warn("⚠️ 비밀번호가 존재하지 않음. userId: {}", userId);
                throw new BadRequestException("입력한 비밀번호가 존재하지 않습니다.");
            }

            String currentPassword = userMapper.selectCurrentPassword(userId);

            if(!bCryptPasswordEncoder.matches(inputPassword, currentPassword)) {
                log.warn("⚠️ 비밀번호 불일치. inputPassword: {}, currentPassword: {}", inputPassword, currentPassword);
                throw new BadRequestException("비밀번호가 일치하지 않습니다.");
            }

            log.info("✅ 현재 비밀번호 조회 성공. userId: {}", userId);
        } catch (Exception e) {
            log.error("❌ 비밀번호 조회 실패. userId: {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    // 비밀번호 수정
    @Override
    public void putMyPagePassword(int userId, String newPassword) {
        try {
            log.info("💡 비밀번호 수정 시작. userId: {}", userId);
            if(newPassword == null || newPassword.isEmpty()) {
                log.warn("⚠️ 새 비밀번호가 존재하지 않음. userId: {}", userId);
                throw new BadRequestException("입력한 새 비밀번호가 존재하지 않습니다.");
            }

            userMapper.updateMyPagePassword(userId, bCryptPasswordEncoder.encode(newPassword));
            log.info("✅ 비밀번호 수정 성공. userId: {}", userId);
        } catch (Exception e) {
            log.error("❌ 비밀번호 수정 실패. userId: {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    // ==========================================
    //               Signup / Login
    // ==========================================
    // 회원가입
    @Override
    public void signupUser(User user) {
        User existingEmail = userMapper.selectUserByUserEmail(user.getUserEmail());

        if(existingEmail != null) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        String existingPhone = userMapper.selectUserByUserPhone(user.getUserPhone());
        if(existingPhone != null) {
            throw new RuntimeException("이미 존재하는 전화번호입니다.");
        }

        String encodePw = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodePw);
        userMapper.insertUser(user);
        log.info("회원가입 완료 - 이메일 {}, 사용자명 : {}", user.getUserEmail(), user.getUserName());
    }

    // 로그인 시 토큰 처리 (컨트롤러에서)
    // 로그인
    @Override
    public User userLogin(String userEmail, String userPassword) {
        User user = userMapper.selectUserByUserEmail(userEmail);

        if(user == null){
            log.warn("로그인 실패 - 존재하지 않는 이메일 : {}", userEmail);
            return null;
        }

        if(!bCryptPasswordEncoder.matches(userPassword, user.getUserPassword())) {
            log.warn("로그인 실패 - 잘못된 비밀번호 : {}", userEmail);
            return null;
        }

        user.setUserPassword(null);
        log.info("로그인 성공 - 이메일 : {}", userEmail);
        return user;
    }

    // 아이디 찾기
    @Override
    public String getUserFindId(String userName, String userPhone) {
        return "";
    }

    // 비밀번호 찾기
    @Override
    public Integer getUserFindPassword(String userEmail, String userBirth) {
        User user = userMapper.selectUserByUserEmail(userEmail);

        if(user == null){
            log.warn("존재하지 않는 이메일 : {}", userEmail);
            return null;
        }
        if(!userBirth.equals(user.getUserBirth())) {
            log.warn("존재하지 않는 생년 : {}", userBirth);
            return null;
        }

        return user.getUserId();
    }

    // 비밀번호 변경
    @Override
    public int putUserPassword(Long userId, String userPassword) {
        return 0;
    }

    @Override
    public User getUserByUserEmail(String userEmail) {
        return null;
    }
    @Override
    public String getUserByUserPhone(String userPhone) {
        return "";
    }
}
