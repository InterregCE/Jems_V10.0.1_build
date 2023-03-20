package io.cloudflight.jems.server.notification.inApp.service.getMyNotification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyNotification(
    private val persistence: NotificationPersistence,
    private val securityService: SecurityService,
) : GetMyNotificationInteractor {

    // intentionally not secured
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<UserNotification> =
        persistence.getUserNotifications(securityService.getUserIdOrThrow(), pageable)

}
