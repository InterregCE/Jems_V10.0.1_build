package io.cloudflight.jems.server.project.entity.workpackage.activity

import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class WorkPackageActivityPartnerId(

    @field:NotNull
    val activityId: Long,

    @field:NotNull
    val projectPartnerId: Long

)
