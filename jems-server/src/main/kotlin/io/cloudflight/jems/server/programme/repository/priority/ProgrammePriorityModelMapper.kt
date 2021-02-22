package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityTranslEntity
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityTranslId
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective

fun ProgrammePriority.toEntity() = ProgrammePriorityEntity(
    id = id ?: 0,
    code = code,
    // translatedValues - needs programmePriorityId
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
    title = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.title) },
    objective = objective,
    specificObjectives = specificObjectives.map { it.toModel() }.sortedBy { it.programmeObjectivePolicy }
)

fun ProgrammeSpecificObjectiveEntity.toModel() = ProgrammeSpecificObjective(
    code = code,
    programmeObjectivePolicy = programmeObjectivePolicy,
)

fun combineTranslatedValues(
    programmePriorityId: Long,
    title: Set<InputTranslation>
): MutableSet<ProgrammePriorityTranslEntity> {
    val titleMap = title.associateBy( { it.language }, { it.translation } )
    val languages = titleMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProgrammePriorityTranslEntity(
            ProgrammePriorityTranslId(programmePriorityId, it),
            titleMap[it]
        )
    }
}

