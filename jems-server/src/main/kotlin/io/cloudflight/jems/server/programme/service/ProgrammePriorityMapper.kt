package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity

fun ProgrammePriorityEntity.toOutputProgrammePrioritySimple() = OutputProgrammePrioritySimple(
    code = code,
    title = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.title) },
)

fun ProgrammeSpecificObjectiveEntity.toOutputProgrammePriorityPolicy() = OutputProgrammePriorityPolicySimpleDTO(
    programmeObjectivePolicy = programmeObjectivePolicy,
    code = code
)
