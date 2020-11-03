package io.cloudflight.jems.api.programme.dto.priority

import io.cloudflight.jems.api.programme.validator.PriorityPoliciesHaveUniqueCodes
import io.cloudflight.jems.api.programme.validator.PriorityPoliciesHaveValidObjectives
import io.cloudflight.jems.api.programme.validator.UniqueProgrammePriorityCodeAndTitle
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@UniqueProgrammePriorityCodeAndTitle // should be applied first
@PriorityPoliciesHaveValidObjectives
@PriorityPoliciesHaveUniqueCodes
data class InputProgrammePriorityUpdate(

    @field:NotNull(message = "common.id.should.not.be.empty")
    val id: Long,

    @field:NotBlank(message = "programme.priority.code.should.not.be.empty")
    @field:Size(max = 50, message = "programme.priority.code.size.too.long")
    val code: String?,

    @field:NotBlank(message = "programme.priority.title.should.not.be.empty")
    @field:Size(max = 255, message = "programme.priority.title.size.too.long")
    val title: String?,

    @field:NotNull(message = "programme.priority.objective.should.not.be.empty")
    val objective: ProgrammeObjective?,

    @field:NotNull(message = "programme.priority.priorityPolicies.should.not.be.null")
    val programmePriorityPolicies: Set<InputProgrammePriorityPolicy>?

)
