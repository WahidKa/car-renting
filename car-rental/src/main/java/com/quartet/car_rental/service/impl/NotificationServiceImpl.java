package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.NotificationRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.Notification;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dto.response.NotificationResponse;
import com.quartet.car_rental.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Override
    public List<NotificationResponse> getUserNotifications(String email) {
        logger.info("### service - Get User Notifications - Begin ###");

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            logger.info("User not found: {}", email);
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());

        logger.info("### service - Get User Notifications - End ###");
        return notifications.stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse convertToNotificationResponse(Notification notification) {
        if (notification.getBooking() != null) {
            return new NotificationResponse(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getTimestamp(),
                    notification.isSeen(),
                    notification.getBooking().getId(),
                    notification.getBooking().getStatus().toString()
            );
        } else {
            return new NotificationResponse(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getTimestamp(),
                    notification.isSeen()
            );
        }
    }
}
