package com.meomulm.notification.model.service;

import com.meomulm.notification.model.dto.Notification;
import com.meomulm.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public List<Notification> selectNotificationByUserId(int userId) {
        log.info("💡 회원 알림 내역 조회 시작. userId: {}", userId);
        List<Notification> notifications = notificationMapper.selectNotificationByUserId(userId);
        log.info("✅ 회원 알림 내역 조회 성공: {}", notifications);
        return notifications;
    }

    @Override
    public void insertNotification(Notification notification) {
        log.info("💡 회원 알림 내역 추가 시작. userId: {}", notification);
        int result = notificationMapper.insertNotification(notification);
        log.info("✅ 회원 알림 내역 추가 결과: {}", result != 1 ? "failed" : "successful");
    }

    @Override
    public void updateNotificationStatus(int notificationId, int currentUserId) {
        log.info("💡 회원 알림 상태 변경 시작. notificationId: {}, currentUserId: {}", notificationId, currentUserId);
        int result = notificationMapper.updateNotificationStatus(notificationId, currentUserId);
        log.info("✅ 회원 알림 상태 변경 결과: {}", result != 1 ? "failed" : "successful");
    }

    @Override
    public void deleteNotification(int notificationId, int currentUserId) {
        log.info("💡 회원 알림 삭제 시작. notificationId: {}, currentUserId: {}", notificationId, currentUserId);
        int result = notificationMapper.deleteNotification(notificationId, currentUserId);
        log.info("✅ 회원 알림 삭제 결과: {}", result != 1 ? "failed" : "successful");
    }
}
