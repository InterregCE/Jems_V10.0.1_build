package io.cloudflight.jems.server.project.entity.workpackage.output

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class WorkPackageOutputId(

    @field:NotNull
    val workPackageId: Long,

    @field:NotNull
    val outputNumber: Int

) : Serializable
