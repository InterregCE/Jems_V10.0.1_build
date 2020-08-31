package io.cloudflight.ems.api.workpackage.dto

import javax.validation.constraints.Size

data class InputWorkPackageUpdate(

    val id: Long,

    @field:Size(max = 100, message = "work_package.name.wrong.size")
    val name: String?,

    @field:Size(max = 250, message = "work_package.projectSpecificObjective.wrong.size")
    val specificObjective: String?,

    @field:Size(max = 500, message = "work_package.objectiveAndAudience.wrong.size")
    val objectiveAndAudience: String?
)
