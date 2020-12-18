package io.cloudflight.jems.server.project.entity.workpackage.activity

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class WorkPackageActivityId(

    @field:NotNull
    val workPackageId: Long,

    @field:NotNull
    val activityNumber: Int

) : Serializable
