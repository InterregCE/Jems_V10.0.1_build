package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity

fun ProgrammePriorityEntity.toOutputProgrammePrioritySimple() = OutputProgrammePrioritySimple(
    code = code,
    title = title
)

fun ProgrammeSpecificObjectiveEntity.toOutputProgrammePriorityPolicy() = OutputProgrammePriorityPolicySimple(
    programmeObjectivePolicy = programmeObjectivePolicy,
    code = code
)
