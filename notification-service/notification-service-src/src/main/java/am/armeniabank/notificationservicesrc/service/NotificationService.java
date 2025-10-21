package am.armeniabank.notificationservicesrc.service;

import am.armeniabank.notificationserviceapi.request.NotificationRequest;
import am.armeniabank.notificationserviceapi.response.NotificationResponse;
import am.armeniabank.notificationservicesrc.entity.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(Notification notification);

    NotificationResponse sendNotificationByUser(NotificationRequest notificationRequest);

    List<NotificationResponse> getNotificationByUserId();
}
