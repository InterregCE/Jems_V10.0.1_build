package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityId
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.validation.constraints.NotNull

@Embeddable
data class WorkPackageActivityDeliverableId(

    @Embedded
    val activityId: WorkPackageActivityId,

    @field:NotNull
    val deliverableNumber: Int

) : Serializable
