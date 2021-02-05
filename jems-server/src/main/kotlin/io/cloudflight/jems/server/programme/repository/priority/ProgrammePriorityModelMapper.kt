package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective

fun ProgrammePriority.toEntity() = ProgrammePriorityEntity(
    id = id ?: 0,
    code = code,
    title = title,
    objective = objective,
    specificObjectives = specificObjectives.toEntity()
)

fun Iterable<ProgrammeSpecificObjective>.toEntity() = mapTo(HashSet()) {
    ProgrammeSpecificObjectiveEntity(
        code = it.code,
        programmeObjectivePolicy = it.programmeObjectivePolicy,
    )
}

fun ProgrammePriorityEntity.toModel() = ProgrammePriority(
    id = id,
    code = code,
    title = title,
    objective = objective,
    specificObjectives = specificObjectives.map { it.toModel() }.sortedBy { it.programmeObjectivePolicy }
)

fun ProgrammeSpecificObjectiveEntity.toModel() = ProgrammeSpecificObjective(
    code = code,
    programmeObjectivePolicy = programmeObjectivePolicy,
)
