package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput


fun WorkPackageEntity.toOutputWorkPackageSimple() = OutputWorkPackageSimple(
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) }
)

fun WorkPackageEntity.toOutputWorkPackage() = OutputWorkPackage(
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    specificObjective = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.specificObjective
        )
    },
    objectiveAndAudience = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.objectiveAndAudience
        )
    }
)

fun InputWorkPackageCreate.toEntity(project: ProjectEntity) = WorkPackageEntity(
    translatedValues = mutableSetOf(),
    project = project
).apply {
    translatedValues.addTranslationEntities(
        { language ->
            WorkPackageTransl(
                translationId = TranslationId(this, language),
                name = name.extractTranslation(language),
                specificObjective = specificObjective.extractTranslation(language),
                objectiveAndAudience = objectiveAndAudience.extractTranslation(language)
            )
        }, arrayOf(name, specificObjective, objectiveAndAudience)
    )
}

fun InputWorkPackageUpdate.toTranslatedValues(workPackageEntity: WorkPackageEntity): MutableSet<WorkPackageTransl> =
    mutableSetOf<WorkPackageTransl>().apply {
        this.addTranslationEntities(
            { language ->
                WorkPackageTransl(
                    translationId = TranslationId(workPackageEntity, language),
                    name = name.extractTranslation(language),
                    specificObjective = specificObjective.extractTranslation(language),
                    objectiveAndAudience = objectiveAndAudience.extractTranslation(language)
                )
            }, arrayOf(name, specificObjective, objectiveAndAudience)
        )
    }

fun List<WorkPackageRow>.toOutputWorkPackageHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        OutputWorkPackage(
            id = groupedRows.value.first().id,
            name = groupedRows.value.extractField { it.name },
            specificObjective = groupedRows.value.extractField { it.specificObjective },
            objectiveAndAudience = groupedRows.value.extractField { it.objectiveAndAudience },
            number = groupedRows.value.first().number,
        )
    }.firstOrNull()

fun List<WorkPackageRow>.toOutputWorkPackageSimpleHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        OutputWorkPackageSimple(
            id = groupedRows.value.first().id,
            name = groupedRows.value.extractField { it.name },
            number = groupedRows.value.first().number,
        )
    }

fun List<WorkPackageOutputRow>.toWorkPackageOutputsHistoricalData(periods: List<ProjectPeriodRow>? = null) =
    this.groupBy { it.outputNumber }.map { groupedRows ->
        WorkPackageOutput(
            workPackageId = groupedRows.value.first().workPackageId,
            outputNumber = groupedRows.value.first().outputNumber,
            programmeOutputIndicatorId = groupedRows.value.first().programmeOutputIndicatorId,
            programmeOutputIndicatorIdentifier = groupedRows.value.first().programmeOutputIndicatorIdentifier,
            targetValue = groupedRows.value.first().targetValue,
            periodNumber = groupedRows.value.first().periodNumber,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description },
            programmeOutputIndicatorName = groupedRows.value
                .extractField({ it.programmeOutputIndicatorLanguage }, { it.programmeOutputIndicatorName }),
            programmeOutputIndicatorMeasurementUnit = groupedRows.value
                .extractField({ it.programmeOutputIndicatorLanguage }) { it.programmeOutputIndicatorMeasurementUnit },
            periodStartMonth = periods?.find {period -> period.periodNumber == groupedRows.value.first().periodNumber}?.periodStart,
            periodEndMonth = periods?.find {period -> period.periodNumber == groupedRows.value.first().periodNumber}?.periodEnd,
        )
    }

fun List<WorkPackageRow>.toTimePlanWorkPackageHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectWorkPackage(
            id = groupedRows.value.first().id,
            workPackageNumber = groupedRows.value.first().number!!,
            name = groupedRows.value.extractField { it.name },
            specificObjective = groupedRows.value.extractField { it.specificObjective },
            objectiveAndAudience = groupedRows.value.extractField { it.objectiveAndAudience }
        )
    }.toList()

fun List<WorkPackageOutputRow>.toTimePlanWorkPackageOutputHistoricalData() =
    this.groupBy { Pair(it.outputNumber, it.workPackageId) }.map { groupedRows ->
        WorkPackageOutput(
            workPackageId = groupedRows.value.first().workPackageId,
            outputNumber = groupedRows.value.first().outputNumber,
            programmeOutputIndicatorId = groupedRows.value.first().programmeOutputIndicatorId,
            programmeOutputIndicatorIdentifier = groupedRows.value.first().programmeOutputIndicatorIdentifier,
            targetValue = groupedRows.value.first().targetValue,
            periodNumber = groupedRows.value.first().periodNumber,
            title = groupedRows.value.extractField { it.title },
            description = groupedRows.value.extractField { it.description }
        )
    }

fun toWorkPackageHistorical(
    wpId: Long,
    workPackageRows: List<WorkPackageRow>,
    workPackageActivities: List<WorkPackageActivity>,
    workPackageOutputs: List<WorkPackageOutput>,
    workPackageInvestments: List<WorkPackageInvestment>
): ProjectWorkPackageFull {
    return ProjectWorkPackageFull(
        id = wpId,
        workPackageNumber = workPackageRows.first().number!!,
        name = workPackageRows.extractField { it.name },
        specificObjective = workPackageRows.extractField { it.specificObjective },
        objectiveAndAudience = workPackageRows.extractField { it.objectiveAndAudience },
        activities = workPackageActivities,
        outputs = workPackageOutputs,
        investments = workPackageInvestments,
    )
}
