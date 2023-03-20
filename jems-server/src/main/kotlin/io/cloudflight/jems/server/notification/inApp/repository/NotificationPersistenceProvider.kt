package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.Notification
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class NotificationPersistenceProvider(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val notificationRepository: NotificationRepository,
) : NotificationPersistence {

    @Transactional
    override fun saveNotifications(notification: Notification, recipients: Set<String>) {
        val users = userRepository.findAllByEmailInIgnoreCaseOrderByEmail(recipients)

        val notificationsToSave = notification.toUsers(users = users, projectResolver = { projectRepository.getById(it) })
        notificationRepository.saveAll(notificationsToSave)
    }

    @Transactional(readOnly = true)
    override fun getUserNotifications(userId: Long, pageable: Pageable) =
        notificationRepository.findAllByAccountId(userId, pageable).toModel()

}
