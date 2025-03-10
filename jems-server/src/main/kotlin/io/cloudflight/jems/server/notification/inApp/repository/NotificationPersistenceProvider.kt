package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
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
    override fun saveNotification(notification: NotificationInApp) {
        val recipients = resolveRecipientsOf(notification)
        val notificationsToSave = notification.toUsers(
            recipients = recipients,
            projectResolver = { projectRepository.getReferenceById(it) },
        )
        notificationRepository.saveAll(notificationsToSave)
    }

    @Transactional(readOnly = true)
    override fun getUserNotifications(userId: Long, pageable: Pageable) =
        notificationRepository.findAllByAccountId(userId, pageable).toModel()

    @Transactional
    override fun saveOrUpdateSystemNotification(notification: NotificationInApp) {
        val recipients = resolveRecipientsOf(notification)
        val notificationsToSave = notification.toUsersNonProject(recipients = recipients)

        notificationRepository.deleteAllByGroupIdentifier(notification.groupId)
        notificationRepository.saveAll(notificationsToSave)
    }

    private fun resolveRecipientsOf(notification: NotificationInApp) =
        userRepository.findAllByEmailInIgnoreCaseOrderByEmail(notification.recipientsInApp)

}
