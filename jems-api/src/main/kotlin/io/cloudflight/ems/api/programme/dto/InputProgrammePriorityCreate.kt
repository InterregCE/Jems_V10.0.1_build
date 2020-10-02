package io.cloudflight.ems.api.programme.dto

import io.cloudflight.ems.api.programme.validator.PriorityPoliciesHaveUniqueCodes
import io.cloudflight.ems.api.programme.validator.PriorityPoliciesHaveValidObjectives
import io.cloudflight.ems.api.programme.validator.UniqueProgrammePriorityCodeAndTitle
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@UniqueProgrammePriorityCodeAndTitle // should be applied first
@PriorityPoliciesHaveValidObjectives
@PriorityPoliciesHaveUniqueCodes
data class InputProgrammePriorityCreate(

    @field:NotBlank(message = "programme.priority.code.should.not.be.empty")
    @field:Size(max = 50, message = "programme.priority.code.size.too.long")
    val code: String?,

    @field:NotBlank(message = "programme.priority.title.should.not.be.empty")
    @field:Size(max = 255, message = "programme.priority.title.size.too.long")
    val title: String?,

    @field:NotNull(message = "programme.priority.objective.should.not.be.empty")
    val objective: ProgrammeObjective?,

    @field:NotNull(message = "programme.priority.priorityPolicies.should.not.be.null")
    @field:NotEmpty(message = "programme.priority.priorityPolicies.should.not.be.empty")
    val programmePriorityPolicies: Set<InputProgrammePriorityPolicy>?

)
