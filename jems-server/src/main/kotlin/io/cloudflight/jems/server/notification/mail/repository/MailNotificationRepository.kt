package io.cloudflight.jems.server.notification.mail.repository

import io.cloudflight.jems.server.notification.mail.entity.MailNotificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MailNotificationRepository : JpaRepository<MailNotificationEntity, Long>
