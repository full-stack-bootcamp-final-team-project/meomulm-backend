package com.meomulm.user.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.common.exception.UnauthorizedException;
import com.meomulm.common.util.ValidateUtil;
import com.meomulm.user.model.dto.EmailAuth;
import com.meomulm.user.model.dto.MyReservationResponse;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.mapper.EmailAuthMapper;
import com.meomulm.user.model.mapper.UserMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final EmailAuthMapper emailAuthMapper;           // ✅ 추가
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ValidateUtil validateUtil;
    private final JavaMailSender javaMailSender;

    // ==========================================
    //                  My Page
    // ==========================================

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

    @Transactional
    @Override
    public void putUserInfo(User user, int currentUserId) {
        validateUtil.validateName(user.getUserName());
        validateUtil.validatePhone(user.getUserPhone());

        log.info("💡 회원정보 수정 시작. userId: {}", currentUserId);
        userMapper.updateUserInfo(user.getUserName(), user.getUserPhone(), currentUserId);

        log.info("✅ 회원정보 수정 완료. userId: {}", currentUserId);
    }

    @Override
    public List<MyReservationResponse> getUserReservationById(int userId) {
        log.info("💡 예약내역 조회 시작. userId: {}", userId);
        List<MyReservationResponse> reservations = userMapper.selectUserReservationById(userId);

        if (reservations == null) {
            log.warn("⚠️ 조회 결과 - 예약내역 없음. userId: {}", userId);
            throw new BadRequestException("예약이 존재하지 않습니다.");
        }

        log.info("✅ 예약내역 조회 성공. userId: {}", userId);
        return reservations;
    }

    @Transactional
    @Override
    public void updateProfileImage(String userProfileImage, int userId) {
        log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
        if (userProfileImage == null || userProfileImage.isEmpty()) {
            log.warn("⚠️ 프로필 이미지가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("프로필 사진이 존재하지 않습니다.");
        }

        userMapper.updateProfileImage(userProfileImage, userId);
        log.info("✅ 프로필 사진 수정 성공. userId: {}, userProfileImage: {}", userId, userProfileImage);
    }

    @Override
    public void getCurrentPassword(int userId, String inputPassword) {
        log.info("💡 현재 비밀번호 확인 시작. userId: {}", userId);
        if (inputPassword == null || inputPassword.isEmpty()) {
            log.warn("⚠️ 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("입력한 비밀번호가 존재하지 않습니다.");
        }

        String currentPassword = userMapper.selectCurrentPassword(userId);

        if (!bCryptPasswordEncoder.matches(inputPassword, currentPassword)) {
            log.warn("⚠️ 비밀번호 불일치. userId: {}", userId);
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        log.info("✅ 현재 비밀번호 조회 성공. userId: {}", userId);
    }

    @Transactional
    @Override
    public void putMyPagePassword(int userId, String newPassword) {
        log.info("💡 비밀번호 수정 시작. userId: {}", userId);
        if (newPassword == null || newPassword.isEmpty()) {
            log.warn("⚠️ 새 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("입력한 새 비밀번호가 존재하지 않습니다.");
        }

        validateUtil.validatePassword(newPassword);
        userMapper.updateMyPagePassword(userId, bCryptPasswordEncoder.encode(newPassword));
        log.info("✅ 비밀번호 수정 성공. userId: {}", userId);
    }

    @Transactional
    @Override
    public void deleteUser(int userId) {
        log.info("💡 회원정보 삭제 시작. userId: {}", userId);
        userMapper.deleteUser(userId);
        log.info("✅ 회원정보 삭제 성공. userId: {}", userId);
    }

    // ==========================================
    //               Signup / Login
    // ==========================================

    @Transactional
    @Override
    public void signup(User user) {
        User existingEmail = userMapper.selectUserByUserEmail(user.getUserEmail());
        if (existingEmail != null) {
            log.warn("❌ 이미 존재하는 이메일 : {}", user.getUserEmail());
            throw new NotFoundException("이미 존재하는 이메일입니다.");
        }

        User existingPhone = userMapper.selectUserByUserPhone(user.getUserPhone());
        if (existingPhone != null) {
            log.warn("❌ 이미 존재하는 전화번호 : {}", user.getUserPhone());
            throw new NotFoundException("이미 존재하는 전화번호입니다.");
        }

        validateUtil.validateEmail(user.getUserEmail());
        validateUtil.validatePassword(user.getUserPassword());
        validateUtil.validateName(user.getUserName());
        validateUtil.validatePhone(user.getUserPhone());
        validateUtil.validateBirth(user.getUserBirth());

        String encodePw = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodePw);
        userMapper.insertUser(user);
        log.info("✅ 회원가입 완료 - 이메일 {}, 사용자명 : {}", user.getUserEmail(), user.getUserName());
    }

    @Override
    public User login(String userEmail, String userPassword) {
        User user = userMapper.selectUserLogin(userEmail);

        if (bCryptPasswordEncoder.matches(userPassword, user.getUserPassword())) {
            log.info("✅ 로그인 성공 - 이메일 : {}", userEmail);
            return user;
        }

        throw new NotFoundException("로그인 정보 없음");
    }

    @Override
    public String getUserFindId(String userName, String userPhone) {
        User user = userMapper.selectUserFindId(userName, userPhone);

        if (user != null) {
            log.info("✅ 아이디 찾기 성공 : {}", user.getUserEmail());
            return user.getUserEmail();
        }
        throw new NotFoundException("이메일 정보 없음");
    }

    @Override
    public int getUserFindPassword(String userEmail, String userBirth) {
        User user = userMapper.selectUserFindPassword(userEmail, userBirth);
        if (user == null) throw new NotFoundException("유저 정보 없음");

        try {
            sendEmailAndSaveAuth(userEmail);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    // ==========================================
    //        이메일 인증 (DB 저장 방식) ✅
    // ==========================================

    /**
     * 이메일 인증코드 발송 후 DB에 저장 (UPSERT)
     * user_email이 PK이므로 재발송 시 기존 레코드를 덮어씀
     *
     * @param userEmail 인증코드를 받을 이메일
     * @return 성공 1 / 실패 0
     */
    @Transactional
    @Override
    public int sendEmailAndSaveAuth(String userEmail) {
        // 1. 인증코드 생성 및 이메일 발송
        String authCode = sendEmail(userEmail);

        // 2. DB UPSERT (이미 존재하면 덮어쓰기)
        EmailAuth emailAuth = new EmailAuth();
        emailAuth.setUserEmail(userEmail);
        emailAuth.setAuthCode(authCode);
        emailAuth.setExpireTime(LocalDateTime.now().plusMinutes(5));  // 5분 후 만료

        emailAuthMapper.upsertEmailAuth(emailAuth);
        log.info("✅ 이메일 인증코드 DB 저장 완료 - email: {}", userEmail);

        return 1;
    }

    /**
     * DB에 저장된 인증코드와 비교하여 검증
     * 검증 성공 시 해당 레코드 삭제
     *
     * @param userEmail 이메일
     * @param inputCode 사용자 입력 인증코드
     * @return 성공 1 / 실패 0
     */
    @Transactional
    @Override
    public int verifyEmailCode(String userEmail, String inputCode) {
        EmailAuth emailAuth = emailAuthMapper.selectByEmail(userEmail);

        if (emailAuth == null) {
            log.warn("⚠️ 인증 정보 없음 - email: {}", userEmail);
            return 0;
        }

        // 만료 시간 확인
        if (LocalDateTime.now().isAfter(emailAuth.getExpireTime())) {
            log.warn("⚠️ 인증코드 만료 - email: {}", userEmail);
            emailAuthMapper.deleteByEmail(userEmail);  // 만료된 레코드 정리
            return 0;
        }

        // 인증코드 비교
        if (!emailAuth.getAuthCode().equals(inputCode)) {
            log.warn("⚠️ 인증코드 불일치 - email: {}", userEmail);
            return 0;
        }

        // 검증 성공 → 레코드 삭제 (일회성 인증)
        emailAuthMapper.deleteByEmail(userEmail);
        log.info("✅ 이메일 인증 성공 - email: {}", userEmail);
        return 1;
    }

    /**
     * 이메일 인증코드 생성 및 발송 (내부 전용)
     *
     * @param userEmail 수신 이메일
     * @return 생성된 인증코드
     */
    @Override
    public String sendEmail(String userEmail) {
        // 6자리 숫자 인증코드 생성
        StringBuilder authKey = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            authKey.append((int) (Math.random() * 10));
        }

        try {
            String title = "[MEOMULM] 회원가입 인증번호입니다.";
            String content =
                    "안녕하세요.\n\n" +
                            "회원가입 인증번호는 아래와 같습니다.\n\n" +
                            "인증번호 : " + authKey + "\n\n" +
                            "감사합니다.";

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject(title);
            helper.setText(content, false);

            javaMailSender.send(mimeMessage);
            log.info("✅ 이메일 발송 완료 - email: {}", userEmail);

            return authKey.toString();

        } catch (Exception e) {
            log.error("❌ 이메일 발송 실패 - email: {}", userEmail, e);
            throw new UnauthorizedException("인증 실패");
        }
    }

    /**
     * 비밀번호 변경 (로그인 페이지)
     */
    @Transactional
    @Override
    public void patchUserPassword(String userEmail, String newPassword) {
        log.info("💡 비밀번호 수정 시작 userEmail: {}", userEmail);

        if (newPassword == null || newPassword.isEmpty()) {
            log.warn("⚠️ 새 비밀번호가 존재하지 않음 userEmail: {}", userEmail);
            throw new BadRequestException("새 비밀번호가 존재하지 않습니다.");
        }

        validateUtil.validatePassword(newPassword);

        int result = userMapper.updateUserPassword(userEmail, bCryptPasswordEncoder.encode(newPassword));

        if (result == 0) {
            throw new BadRequestException("비밀번호 변경 실패");
        }
        log.info("✅ 비밀번호 수정 성공 userEmail: {}", userEmail);
    }

    @Override
    public User getUserByUserEmail(String userEmail) {
        return userMapper.selectUserByUserEmail(userEmail);
    }

    @Override
    public boolean existsByUserEmail(String userEmail) {
        return userMapper.existsByUserEmail(userEmail) > 0;
    }

    @Override
    public boolean existsByUserPhone(String userPhone) {
        return userMapper.existsByUserPhone(userPhone) > 0;
    }
}