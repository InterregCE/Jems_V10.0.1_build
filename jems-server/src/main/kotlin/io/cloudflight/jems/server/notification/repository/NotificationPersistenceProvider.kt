package io.cloudflight.jems.server.notification.repository

import io.cloudflight.jems.server.notification.NotificationPersistence
import io.cloudflight.jems.server.notification.model.Notification
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class NotificationPersistenceProvider(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
) : NotificationPersistence {

    @Transactional
    override fun saveNotifications(notifications: List<Notification>) {
        val emails = notifications.mapTo(HashSet()) { it.email }
        val usersByEmail = userRepository.findAllByEmailInIgnoreCaseOrderByEmail(emails).associateBy { it.email }

        val notificationsToSave = notifications.toEntities(creationTime = ZonedDateTime.now(), users = usersByEmail)
        notificationRepository.saveAll(notificationsToSave)
    }
}
