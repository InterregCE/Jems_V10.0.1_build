package io.cloudflight.ems.service

import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityCreate
import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityPolicy
import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityUpdate
import io.cloudflight.ems.api.programme.dto.OutputProgrammePriority
import io.cloudflight.ems.api.programme.dto.OutputProgrammePriorityPolicy
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import io.cloudflight.ems.entity.ProgrammePriority
import io.cloudflight.ems.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.exception.I18nValidationException
import org.springframework.http.HttpStatus

fun ProgrammePriority.toOutputProgrammePriority() = OutputProgrammePriority(
    id = id!!,
    code = code,
    title = title,
    objective = objective,
    programmePriorityPolicies = programmePriorityPolicies.map { it.toOutputProgrammePriorityPolicy() }
)

fun ProgrammePriorityPolicy.toOutputProgrammePriorityPolicy() = OutputProgrammePriorityPolicy(
    programmeObjectivePolicy = programmeObjectivePolicy,
    code = code
)

fun InputProgrammePriorityCreate.toEntity() = ProgrammePriority(
    code = code!!,
    title = title!!,
    objective = objective!!,
    programmePriorityPolicies = programmePriorityPolicies!!.mapTo(HashSet()) { it.toEntity(objective!!) }
)

fun InputProgrammePriorityUpdate.toEntity() = ProgrammePriority(
    id = id,
    code = code!!,
    title = title!!,
    objective = objective!!,
    programmePriorityPolicies = programmePriorityPolicies!!.mapTo(HashSet()) { it.toEntity(objective!!) }
)

fun InputProgrammePriorityPolicy.toEntity(checkObjective: ProgrammeObjective) = ProgrammePriorityPolicy(
    programmeObjectivePolicy = programmeObjectivePolicy!!.ifSuits(checkObjective),
    code = code!!
)

/**
 * Check, if parent objective of this policy is properly set, e.g. if such relation is possible in the system
 */
fun ProgrammeObjectivePolicy.ifSuits(objective: ProgrammeObjective): ProgrammeObjectivePolicy {
    if (this.objective == objective)
        return this
    throw I18nValidationException(
        httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        i18nKey = "programme.priority.priorityPolicies.should.not.be.of.different.objectives"
    )
}
