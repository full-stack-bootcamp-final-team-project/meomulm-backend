package com.meomulm.notification.model.service;

import com.meomulm.notification.model.dto.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> selectNotificationByUserId(int currentUserId);
    void updateNotificationStatus(int notificationId, int currentUserId);
    void deleteNotification(int notificationId, int currentUserId);
}
