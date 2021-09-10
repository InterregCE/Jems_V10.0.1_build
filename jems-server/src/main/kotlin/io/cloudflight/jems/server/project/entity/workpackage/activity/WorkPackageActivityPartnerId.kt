package io.cloudflight.jems.server.project.entity.workpackage.activity

import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageActivityPartnerId(

    @field:NotNull
    val activityId: Long,

    @field:NotNull
    val projectPartnerId: Long

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is WorkPackageActivityPartnerId && activityId == other.activityId && projectPartnerId == other.projectPartnerId

    override fun hashCode(): Int = Objects.hash(activityId, projectPartnerId)

}
