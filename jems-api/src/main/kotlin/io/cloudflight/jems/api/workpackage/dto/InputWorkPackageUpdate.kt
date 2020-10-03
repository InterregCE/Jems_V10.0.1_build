package io.cloudflight.jems.api.workpackage.dto

import javax.validation.constraints.Size

data class InputWorkPackageUpdate(

    val id: Long,

    @field:Size(max = 100, message = "workpackage.name.wrong.size")
    val name: String?,

    @field:Size(max = 250, message = "workpackage.projectSpecificObjective.wrong.size")
    val specificObjective: String?,

    @field:Size(max = 500, message = "workpackage.objectiveAndAudience.wrong.size")
    val objectiveAndAudience: String?
)
