package am.armeniabank.notificationservicesrc.mapper;

import am.armeniabank.notificationserviceapi.enums.Currency;
import am.armeniabank.notificationserviceapi.request.NotificationRequest;
import am.armeniabank.notificationserviceapi.response.NotificationResponse;
import am.armeniabank.notificationservicesrc.entity.Notification;
import am.armeniabank.notificationservicesrc.kafka.event.NotificationEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    default Currency mapCurrency(String currency) {
        return Currency.valueOf(currency.toUpperCase());
    }

    @Mapping(target = "userId", source = "senderId")
    @Mapping(target = "title", expression = "java(\"Transaction completed\")")
    @Mapping(target = "message", expression = "java(String.format(\"You sent %s %s to %s. Your remaining balance: %s %s\", " +
            "dto.getAmount(), dto.getCurrency(), dto.getReceiverName(), dto.getSenderBalanceAfter(), dto.getCurrency()))")
    @Mapping(target = "type", expression = "java(am.armeniabank.notificationserviceapi.enums.NotificationType.TRANSACTIONAL)")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Notification toSenderNotification(NotificationEvent dto);

    @Mapping(target = "userId", source = "receiverId")
    @Mapping(target = "title", expression = "java(\"You received funds\")")
    @Mapping(target = "message", expression = "java(String.format(\"You received %s %s from %s. Your new balance: %s %s\", " +
            "dto.getAmount(), dto.getCurrency(), dto.getSenderName(), dto.getReceiverBalanceAfter(), dto.getCurrency()))")
    @Mapping(target = "type", expression = "java(am.armeniabank.notificationserviceapi.enums.NotificationType.TRANSACTIONAL)")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Notification toReceiverNotification(NotificationEvent dto);

    List<NotificationResponse> mapToListNotification(List<Notification> notifications);

    Notification mapNotification(NotificationRequest notificationRequest);

    NotificationResponse mapToNotificationResponse(Notification saved);

}
