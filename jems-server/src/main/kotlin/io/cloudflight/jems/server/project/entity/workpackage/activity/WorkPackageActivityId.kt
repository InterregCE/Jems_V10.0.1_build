package io.cloudflight.jems.server.project.entity.workpackage.activity

import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageActivityId(

    @field:NotNull
    val workPackageId: Long,

    @field:NotNull
    val activityNumber: Int

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is WorkPackageActivityId && workPackageId == other.workPackageId && activityNumber == other.activityNumber

    override fun hashCode(): Int = Objects.hash(workPackageId, activityNumber)

}
