package io.cloudflight.jems.server.user.entity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class UserProjectId(

    @field:NotNull
    @Column(name = "account_id")
    val userId: Long,

    @field:NotNull
    val projectId: Long,

) : Serializable {

    override fun equals(other: Any?) = this === other ||
        other is UserProjectId &&
        userId == other.userId &&
        projectId == other.projectId

    override fun hashCode() = Objects.hash(userId, projectId)
}
