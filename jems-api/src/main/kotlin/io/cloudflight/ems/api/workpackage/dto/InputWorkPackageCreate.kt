package io.cloudflight.ems.api.workpackage.dto

import javax.validation.constraints.Size
import javax.validation.constraints.NotNull

data class InputWorkPackageCreate(

    @field:Size(max = 100, message = "workpackage.name.wrong.size")
    val name: String?,

    @field:Size(max = 250, message = "workpackage.projectSpecificObjective.wrong.size")
    val specificObjective: String?,

    @field:Size(max = 500, message = "workpackage.objectiveAndAudience.wrong.size")
    val objectiveAndAudience: String?
)
