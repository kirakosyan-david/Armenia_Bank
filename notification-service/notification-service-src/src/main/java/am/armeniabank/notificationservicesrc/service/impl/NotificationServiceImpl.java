package am.armeniabank.notificationservicesrc.service.impl;

import am.armeniabank.armeniabankcommon.contract.UserApi;
import am.armeniabank.armeniabankcommon.response.UserResponse;
import am.armeniabank.notificationserviceapi.request.NotificationRequest;
import am.armeniabank.notificationserviceapi.response.NotificationResponse;
import am.armeniabank.notificationservicesrc.entity.Notification;
import am.armeniabank.notificationservicesrc.mapper.NotificationMapper;
import am.armeniabank.notificationservicesrc.repository.NotificationRepository;
import am.armeniabank.notificationservicesrc.service.NotificationService;
import am.armeniabank.notificationservicesrc.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserApi userApi;

    @Override
    public void sendNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("User notification {}: {}", saved.getUserId(), saved.getMessage());
    }

    @Override
    public NotificationResponse sendNotificationByUser(NotificationRequest notificationRequest) {
        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        UserResponse user = userApi.getUserById(userId, "Bearer " + token);

        Notification notification = notificationMapper.mapNotification(notificationRequest);
        notification.setUserId(userId);

        Notification saved = notificationRepository.save(notification);

        log.info("Notification sent to user {} {} ({}): {}",
                user.getFirstName(), user.getLastName(), userId, saved.getMessage());
        return notificationMapper.mapToNotificationResponse(saved);
    }

    @Override
    public List<NotificationResponse> getNotificationByUserId(){
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        List<Notification> notifications = notificationRepository.findByUserId(currentUserId);
        return notificationMapper.mapToListNotification(notifications);
    }
}
