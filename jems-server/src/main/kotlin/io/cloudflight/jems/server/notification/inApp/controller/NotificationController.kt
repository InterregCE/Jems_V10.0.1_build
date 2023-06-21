package io.cloudflight.jems.server.notification.inApp.controller

import io.cloudflight.jems.api.notification.NotificationApi
import io.cloudflight.jems.server.notification.inApp.service.getMyNotification.GetMyNotificationInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(
    private val getMyNotification: GetMyNotificationInteractor,
): NotificationApi {

    override fun getMyNotifications(pageable: Pageable) =
        getMyNotification.get(pageable).toModel()

}
