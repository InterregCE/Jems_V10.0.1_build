package io.cloudflight.ems.api.workpackage.dto

import javax.validation.constraints.Size
import javax.validation.constraints.NotNull

data class InputWorkPackageCreate(

    @field:NotNull(message = "work_package.number.should.not.be.empty")
    val number: Int,

    @field:Size(max = 100, message = "work_package.name.wrong.size")
    val name: String?,

    @field:Size(max = 250, message = "work_package.projectSpecificObjective.wrong.size")
    val specificObjective: String?,

    @field:Size(max = 500, message = "work_package.objectiveAndAudience.wrong.size")
    val objectiveAndAudience: String?,

    @field:NotNull(message = "work_package.project.should.not.be.empty")
    val projectId: Long?
)
