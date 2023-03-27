package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class NotificationPersistenceProvider(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val notificationRepository: NotificationRepository,
) : NotificationPersistence {

    @Transactional
    override fun saveNotification(notification: NotificationInApp) {
        val notificationsToSave = notification.toUsers(
            groupId = UUID.randomUUID(),
            recipientsResolver = { userRepository.findAllByEmailInIgnoreCaseOrderByEmail(it) },
            projectResolver = { projectRepository.getById(it) },
        )
        notificationRepository.saveAll(notificationsToSave)
    }

    @Transactional(readOnly = true)
    override fun getUserNotifications(userId: Long, pageable: Pageable) =
        notificationRepository.findAllByAccountId(userId, pageable).toModel()

}
