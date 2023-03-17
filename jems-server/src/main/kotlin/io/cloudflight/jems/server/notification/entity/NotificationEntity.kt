package io.cloudflight.jems.server.notification.entity

import io.cloudflight.jems.server.notification.model.NotificationType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull
import javax.persistence.Id

@Entity(name = "notification")
class NotificationEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @field:NotNull
    var userEntity: UserEntity,

    @field:NotNull
    val created: ZonedDateTime,

    val projectId: Long?,
    val projectIdentifier: String?,
    val projectAcronym: String?,

    @field:NotNull
    val subject: String,

    @field:NotNull
    val body: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: NotificationType
)
