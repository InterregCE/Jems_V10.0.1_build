package io.cloudflight.jems.server.programme.controller.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityAvailableSetupDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeSpecificObjectiveDTO
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriorityAvailableSetup
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective

fun ProgrammePriorityDTO.toModel() = ProgrammePriority(
    code = code,
    title = title,
    objective = objective,
    specificObjectives = specificObjectives.map { it.toModel() }
)

fun ProgrammeSpecificObjectiveDTO.toModel() = ProgrammeSpecificObjective(
    code = code,
    programmeObjectivePolicy = programmeObjectivePolicy,
)

fun ProgrammePriority.toDto() = ProgrammePriorityDTO(
    id = id,
    code = code,
    title = title,
    objective = objective,
    specificObjectives = specificObjectives.map { it.toDto() }
)

fun ProgrammeSpecificObjective.toDto() = ProgrammeSpecificObjectiveDTO(
    code = code,
    programmeObjectivePolicy = programmeObjectivePolicy,
    officialCode = programmeObjectivePolicy.officialProgrammePolicyCode
)

fun ProgrammePriorityAvailableSetup.toDto() = ProgrammePriorityAvailableSetupDTO(
    freePrioritiesWithPolicies = freePrioritiesWithPolicies.mapValues { entry -> entry.value.map {
            ProgrammeSpecificObjectiveDTO(
                it,
                "",
                it.officialProgrammePolicyCode)
        } },
    objectivePoliciesAlreadyInUse = objectivePoliciesAlreadyInUse,
)
