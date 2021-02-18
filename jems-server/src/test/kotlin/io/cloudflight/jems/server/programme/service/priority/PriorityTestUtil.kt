package io.cloudflight.jems.server.programme.service.priority

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.GreenUrban
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import java.util.stream.Collectors

val testPriority = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(programmeObjectivePolicy = GreenUrban, code = "GU"),
        ProgrammeSpecificObjective(programmeObjectivePolicy = RenewableEnergy, code = "RE"),
    ),
)

fun getStringOfLength(length: Int): String =
    IntArray(length).map { "x" }.stream().collect(Collectors.joining())
