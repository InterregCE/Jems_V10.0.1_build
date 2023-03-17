package io.cloudflight.jems.server.notification.inApp.entity

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.LocalDateTime
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
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs

@Entity(name = "notification")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "NotificationEntity.withProject",
        attributeNodes = [
            NamedAttributeNode(value = "project"),
        ],
    )
)
class NotificationEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val account: UserEntity,

    @field:NotNull
    val created: LocalDateTime,

    @ManyToOne(optional = true)
    @JoinColumn(name = "project_id")
    val project: ProjectEntity?,
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
