package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetailHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue


fun WorkPackageEntity.toOutputWorkPackageSimple() = OutputWorkPackageSimple (
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) }
)

fun WorkPackageEntity.toOutputWorkPackage() = OutputWorkPackage (
    id = id,
    number = number,
    name = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    specificObjective = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specificObjective) },
    objectiveAndAudience = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.objectiveAndAudience) }
)

fun InputWorkPackageCreate.toEntity(project: ProjectEntity) = WorkPackageEntity (
    project = project
)

fun InputWorkPackageCreate.combineTranslatedValues(
    workPackageId: Long
): MutableSet<WorkPackageTransl> {
    val nameMap = name.associateBy( { it.language }, { it.translation } )
    val specificObjectiveMap = specificObjective.associateBy( { it.language }, { it.translation } )
    val objectiveAndAudienceMap = objectiveAndAudience.associateBy( { it.language }, { it.translation } )
    val languages = nameMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        WorkPackageTransl(
            TranslationWorkPackageId(workPackageId, it),
            nameMap[it],
            specificObjectiveMap[it],
            objectiveAndAudienceMap[it]
        )
    }
}

fun InputWorkPackageUpdate.combineTranslatedValues(
    workPackageId: Long
): MutableSet<WorkPackageTransl> {
    val nameMap = name.associateBy( { it.language }, { it.translation } )
    val specificObjectiveMap = specificObjective.associateBy( { it.language }, { it.translation } )
    val objectiveAndAudienceMap = objectiveAndAudience.associateBy( { it.language }, { it.translation } )
    val languages = nameMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        WorkPackageTransl(
            TranslationWorkPackageId(workPackageId, it),
            nameMap[it],
            specificObjectiveMap[it],
            objectiveAndAudienceMap[it]
        )
    }
}

fun List<WorkPackageRow>.toOutputWorkPackageHistoricalData() =
    this.groupBy { it.id }.map { groupedRows -> OutputWorkPackage(
        id = groupedRows.value.first().id,
        name = groupedRows.value.extractField { it.name },
        specificObjective = groupedRows.value.extractField { it.specificObjective },
        objectiveAndAudience = groupedRows.value.extractField { it.objectiveAndAudience },
        number = groupedRows.value.first().number,
    ) }.first()

fun  List<WorkPackageRow>.toOutputWorkPackageSimpleHistoricalData() =
    this.groupBy { it.id }.map { groupedRows -> OutputWorkPackageSimple(
        id = groupedRows.value.first().id,
        name = groupedRows.value.extractField { it.name },
        number = groupedRows.value.first().number,
    ) }

fun List<WorkPackageOutputRow>.toWorkPackageOutputsHistoricalData() =
    this.groupBy { it.outputNumber }.map { groupedRows -> WorkPackageOutput(
        outputNumber = groupedRows.value.first().outputNumber,
        programmeOutputIndicatorId = groupedRows.value.first().programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = groupedRows.value.first().programmeOutputIndicatorIdentifier,
        targetValue = groupedRows.value.first().targetValue,
        periodNumber = groupedRows.value.first().periodNumber,
        translatedValues = groupedRows.value.mapTo(HashSet()) { WorkPackageOutputTranslatedValue(
            language = it.language!!,
            description = it.description,
            title = it.title
        ) }
    ) }