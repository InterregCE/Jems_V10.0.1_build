package io.cloudflight.jems.server.notification.repository

import io.cloudflight.jems.server.notification.NotificationPersistence
import io.cloudflight.jems.server.notification.model.Notification
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class NotificationPersistenceProvider(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
    ) : NotificationPersistence {

    @Transactional
    override fun saveNotification(projectId: Long, notifications: List<Notification>) {
        val project = projectRepository.getById(projectId)
        val emails = notifications.mapTo(HashSet()) { it.email }
        val users = userRepository.findAllByEmailInIgnoreCaseOrderByEmail(emails).associateBy { it.email }
        val notificationsToSave = notifications.toEntities(project, users)
        notificationRepository.saveAll(notificationsToSave)
    }
}
