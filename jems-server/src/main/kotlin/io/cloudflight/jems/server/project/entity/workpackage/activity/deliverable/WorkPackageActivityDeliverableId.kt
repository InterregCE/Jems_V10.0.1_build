package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityId
import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageActivityDeliverableId(

    @Embedded
    @field:NotNull
    val activityId: WorkPackageActivityId,

    @Column(name = "deliverable_number")
    @field:NotNull
    val deliverableNumber: Int

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is WorkPackageActivityDeliverableId && activityId == other.activityId && deliverableNumber == other.deliverableNumber

    override fun hashCode(): Int = Objects.hash(activityId.hashCode(), deliverableNumber)
}
