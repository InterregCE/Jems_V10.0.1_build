package io.cloudflight.jems.server.programme.service.priority

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.GreenInfrastructure
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import java.util.stream.Collectors

val testPriority = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.EconomicActivity to listOf("001", "002"))
        ),
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = RenewableEnergy,
            code = "RE",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.FormOfSupport to listOf("003", "004"))
        ),
    ),
)

val testPriorityWithWrongTypeOfIntervention = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.TypesOfIntervention to listOf("183"))
        )
    ),
)

val testPriorityWithWrongFormOfSupport = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.FormOfSupport to listOf("007"))
        )
    ),
)

val testPriorityWithWrongTerritorialDeliveryMechanism = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.TerritorialDeliveryMechanism to listOf("034"))
        )
    ),
)

val testPriorityWithWrongEconomicActivity = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.EconomicActivity to listOf("027"))
        )
    ),
)

val testPriorityWithWrongGenderEquality = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.GenderEquality to listOf("004"))
        )
    ),
)

val testPriorityWithWrongRegionalAndSeaBasinStrategy = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.RegionalAndSeaBasinStrategy to listOf("012"))
        )
    ),
)

val testPriorityWithTextCode = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.TypesOfIntervention to listOf("asd"))
        )
    ),
)

val testPriorityWithZeroCode = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.TypesOfIntervention to listOf("000"))
        )
    ),
)

val testPriorityWithNoCode = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.TypesOfIntervention to emptyList())
        )
    ),
)

val testPriorityWithManyCodes = ProgrammePriority(
    id = 2,
    code = "PO-02",
    title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
    objective = ProgrammeObjective.PO2,
    specificObjectives = listOf(
        ProgrammeSpecificObjective(
            programmeObjectivePolicy = GreenInfrastructure,
            code = "GU",
            dimensionCodes = mapOf(ProgrammeObjectiveDimension.TypesOfIntervention to listOf(
                "001",
                "002",
                "003",
                "004",
                "005",
                "006",
                "007",
                "008",
                "009",
                "010",
                "011",
                "012",
                "013",
                "014",
                "015",
                "016",
                "017",
                "018",
                "019",
                "020",
                "021",
            ))
        )
    ),
)

fun getStringOfLength(length: Int): String =
    IntArray(length).map { "x" }.stream().collect(Collectors.joining())
