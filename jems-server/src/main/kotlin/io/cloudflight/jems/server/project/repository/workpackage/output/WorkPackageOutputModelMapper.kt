package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputTransl
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import kotlin.collections.HashSet

fun WorkPackageOutput.toEntity(
    indicatorOutput: IndicatorOutput?,
    workPackage: WorkPackageEntity,
    projectPeriod: ProjectPeriodEntity?,
    outputNumber: Int
) : WorkPackageOutputEntity {
    val outputId = WorkPackageOutputId(workPackage.id, outputNumber)
    return WorkPackageOutputEntity(
        outputId = outputId,
        programmeOutputIndicator = indicatorOutput,
        translatedValues = translatedValues.toEntity(outputId),
        targetValue = targetValue,
        period = projectPeriod
    )
}

fun WorkPackageOutputEntity.toWorkPackageOutput() = WorkPackageOutput(
    outputNumber = outputId.outputNumber,
    programmeOutputIndicatorId = programmeOutputIndicator?.id,
    translatedValues = translatedValues.toModel(),
    targetValue = targetValue,
    periodNumber = period?.id?.number
)

fun Iterable<WorkPackageOutputEntity>.toModel() =
    this.map { it.toWorkPackageOutput() }.sortedBy { it.outputNumber }.toList()

fun Set<WorkPackageOutputTranslatedValue>.toEntity(outputId: WorkPackageOutputId) = mapTo(HashSet()) { it.toEntity(outputId) }

fun WorkPackageOutputTranslatedValue.toEntity(workPackageOutputId: WorkPackageOutputId) = WorkPackageOutputTransl(
    translationId = TranslationWorkPackageOutputId(workPackageOutputId = workPackageOutputId, language = language),
    title = title,
    description = description,
)

fun Set<WorkPackageOutputTransl>.toModel() = mapTo(HashSet()) {
    WorkPackageOutputTranslatedValue(
        language = it.translationId.language,
        description = it.description,
        title = it.title
    )
}
