package io.cloudflight.jems.server.notification.inApp.service.getMyNotification

import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetMyNotificationInteractor {

    fun get(pageable: Pageable): Page<UserNotification>

}
