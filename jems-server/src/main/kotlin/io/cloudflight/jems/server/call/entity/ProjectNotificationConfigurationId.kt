package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectNotificationConfigurationId (

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val id: NotificationType,

    @ManyToOne
    @JoinColumn(name = "call_id", referencedColumnName = "id")
    @field:NotNull
    val callEntity: CallEntity

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectNotificationConfigurationId && id == other.id && callEntity == other.callEntity

    override fun hashCode(): Int = Objects.hash(id, callEntity)

}
