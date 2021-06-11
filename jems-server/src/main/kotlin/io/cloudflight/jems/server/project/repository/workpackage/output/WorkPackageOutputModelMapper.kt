package io.cloudflight.jems.server.project.repository.workpackage.output

import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputTransl
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import kotlin.collections.HashSet

fun WorkPackageOutput.toEntity(
    workPackageId: Long,
    index: Int,
    resolveProgrammeIndicatorEntity: (Long?) -> OutputIndicatorEntity?,
) : WorkPackageOutputEntity {
    val outputId = WorkPackageOutputId(workPackageId, index)
    return WorkPackageOutputEntity(
        outputId = outputId,
        translatedValues = translatedValues.toEntity(outputId),
        periodNumber = periodNumber,
        programmeOutputIndicatorEntity = resolveProgrammeIndicatorEntity.invoke(programmeOutputIndicatorId),
        targetValue = targetValue
    )
}

fun List<WorkPackageOutput>.toIndexedEntity(
    workPackageId: Long,
    resolveProgrammeIndicatorEntity: (Long?) -> OutputIndicatorEntity?,
) = mapIndexed { index, output -> output.toEntity(workPackageId, index.plus(1), resolveProgrammeIndicatorEntity) }

fun Iterable<WorkPackageOutputEntity>.toModel() = map {
    WorkPackageOutput(
        workPackageId = it.outputId.workPackageId,
        outputNumber = it.outputId.outputNumber,
        translatedValues = it.translatedValues.toModel(),
        periodNumber = it.periodNumber,
        programmeOutputIndicatorId = it.programmeOutputIndicatorEntity?.id,
        programmeOutputIndicatorIdentifier = it.programmeOutputIndicatorEntity?.identifier,
        targetValue = it.targetValue
    )
}.sortedBy { it.outputNumber }

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
