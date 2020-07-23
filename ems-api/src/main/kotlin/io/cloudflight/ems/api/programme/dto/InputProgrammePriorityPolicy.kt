package io.cloudflight.ems.api.programme.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputProgrammePriorityPolicy(

    @field:NotNull(message = "programme.priorityPolicy.programmeObjectivePolicy.should.not.be.empty")
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,

    @field:NotBlank(message = "programme.priorityPolicy.code.should.not.be.empty")
    @field:Size(max = 50, message = "programme.priority.title.size.too.long")
    val code: String?

)
