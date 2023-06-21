package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.entity.NotificationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<NotificationEntity, Long> {

    @EntityGraph("NotificationEntity.withProject")
    fun findAllByAccountId(userId: Long, pageable: Pageable): Page<NotificationEntity>

}
