package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistRow
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.toHeadline
import io.cloudflight.jems.server.programme.service.checklist.model.toJson
import io.cloudflight.jems.server.programme.service.checklist.model.toOptionsToggle
import io.cloudflight.jems.server.programme.service.checklist.model.toScore
import io.cloudflight.jems.server.programme.service.checklist.model.toTextInput

fun ProgrammeChecklistEntity.toModel(): ProgrammeChecklist =
    ProgrammeChecklist(
        id = id,
        type = type,
        name = name,
        minScore = minScore,
        maxScore = maxScore,
        allowsDecimalScore = allowsDecimalScore,
        lastModificationDate = lastModificationDate,
        locked = false
    )

fun ProgrammeChecklistEntity.toDetailModel(): ProgrammeChecklistDetail =
    ProgrammeChecklistDetail(
        id = id,
        type = type,
        name = name,
        minScore = minScore,
        maxScore = maxScore,
        allowsDecimalScore = allowsDecimalScore,
        lastModificationDate = lastModificationDate,
        locked = false,
        components = components?.map { it.toModel() }?.sortedBy { it.position }
    )

fun Iterable<ProgrammeChecklistRow>.toModel() = map { it.toModel() }

fun ProgrammeChecklistRow.toModel(): ProgrammeChecklist =
    ProgrammeChecklist(
        id = id,
        type = type,
        name = name,
        minScore = minScore,
        maxScore = maxScore,
        allowsDecimalScore = allowsDecimalScore,
        lastModificationDate = lastModificationDate,
        locked = instancesCount > 0
    )

fun ProgrammeChecklistDetail.toEntity(): ProgrammeChecklistEntity =
    ProgrammeChecklistEntity(
        id = id ?: 0L,
        type = type,
        name = name,
        minScore = minScore,
        maxScore = maxScore,
        allowsDecimalScore = allowsDecimalScore,
        lastModificationDate = lastModificationDate,
        components = components?.map { it.toEntity() }?.toMutableSet()
    ).apply {
        this.assignComponents()
    }

fun ProgrammeChecklistComponentEntity.toModel(): ProgrammeChecklistComponent =
    ProgrammeChecklistComponent(
        id = id,
        type = type,
        position = positionOnTable,
        metadata = toModelMetadata()
    )

fun ProgrammeChecklistComponentEntity.toModelMetadata(): ProgrammeChecklistMetadata =
    when (this.type) {
        ProgrammeChecklistComponentType.HEADLINE -> this.metadata.toHeadline()
        ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> this.metadata.toOptionsToggle()
        ProgrammeChecklistComponentType.TEXT_INPUT -> this.metadata.toTextInput()
        ProgrammeChecklistComponentType.SCORE -> this.metadata.toScore()
    }

fun ProgrammeChecklistComponent.toEntity(): ProgrammeChecklistComponentEntity =
    ProgrammeChecklistComponentEntity(
        id = id ?: 0L,
        type = type,
        positionOnTable = position,
        metadata = metadata.toJson()
    )

fun ProgrammeChecklistComponent.toEntity(checklist: ProgrammeChecklistEntity): ProgrammeChecklistComponentEntity =
    ProgrammeChecklistComponentEntity(
        id = 0L,
        type = type,
        positionOnTable = position,
        metadata = metadata.toJson(),
        checklist = checklist
    )

private fun ProgrammeChecklistEntity.assignComponents() =
    this.components?.forEach { it.checklist = this }
