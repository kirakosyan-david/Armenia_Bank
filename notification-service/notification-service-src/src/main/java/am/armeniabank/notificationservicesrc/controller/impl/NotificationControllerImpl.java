package am.armeniabank.notificationservicesrc.controller.impl;

import am.armeniabank.notificationserviceapi.contract.NotificationController;
import am.armeniabank.notificationserviceapi.request.NotificationRequest;
import am.armeniabank.notificationserviceapi.response.NotificationResponse;
import am.armeniabank.notificationservicesrc.controller.BaseController;
import am.armeniabank.notificationservicesrc.entity.Notification;
import am.armeniabank.notificationservicesrc.mapper.NotificationMapper;
import am.armeniabank.notificationservicesrc.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl extends BaseController implements NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;


    @Override
    public ResponseEntity<NotificationResponse> createNotification(NotificationRequest notification) {
        NotificationResponse notificationResponse = notificationService.sendNotificationByUser(notification);
        return ResponseEntity.ok(notificationResponse);
    }

    @Override
    public ResponseEntity<NotificationResponse> sendNotification(NotificationRequest notification) {
        Notification mapNotification = notificationMapper.mapNotification(notification);
        notificationService.sendNotification(mapNotification);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public ResponseEntity<List<NotificationResponse>> getNotificationByUserId() {
        List<NotificationResponse> notification = notificationService.getNotificationByUserId();
        return ResponseEntity.ok(notification);
    }
}
