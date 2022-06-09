package io.cloudflight.jems.server.project.repository.checklist

import io.cloudflight.jems.server.common.gson.*
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.repository.checklist.toModelMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ChecklistInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceEntity
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceId
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

fun ChecklistInstanceEntity.toModel(): ChecklistInstance =
    ChecklistInstance(
        id,
        status,
        programmeChecklist.type,
        programmeChecklist.name,
        creator.email,
        relatedToId,
        finishedDate,
        programmeChecklist.id,
        consolidated,
        visible
    )

fun List<ChecklistInstanceEntity>.toModel(): List<ChecklistInstance> = this.map { it.toModel() }

fun ChecklistInstanceEntity.toDetailModel(): ChecklistInstanceDetail {
    return ChecklistInstanceDetail(
        id = id,
        programmeChecklistId = programmeChecklist.id,
        status = status,
        type = programmeChecklist.type,
        name = programmeChecklist.name,
        creatorEmail = creator.email,
        creatorId = creator.id,
        finishedDate = finishedDate,
        relatedToId = relatedToId,
        consolidated = consolidated,
        visible = visible,
        minScore = programmeChecklist.minScore,
        maxScore = programmeChecklist.maxScore,
        allowsDecimalScore = programmeChecklist.allowsDecimalScore,
        components = components?.map { it.toModel() }
    )
}

fun ProgrammeChecklistComponentEntity.toInstanceEntity(): ChecklistComponentInstanceEntity {
    return ChecklistComponentInstanceEntity(
        programmeChecklistComponentEntity = this,
        metadata = null,
        checklistComponentId = ChecklistComponentInstanceId(this.id)
    )
}

private fun ChecklistComponentInstanceEntity.toModel(): ChecklistComponentInstance =
    ChecklistComponentInstance(
        id = checklistComponentId.programmeComponentId,
        type = programmeChecklistComponentEntity.type,
        position = programmeChecklistComponentEntity.positionOnTable,
        programmeMetadata = toModelProgrammeMetadata(),
        instanceMetadata = toModelInstanceMetadata()
    )

private fun ChecklistComponentInstanceEntity.toModelProgrammeMetadata(): ProgrammeChecklistMetadata =
    this.programmeChecklistComponentEntity.toModelMetadata()

private fun ChecklistComponentInstanceEntity.toModelInstanceMetadata(): ChecklistInstanceMetadata? =
    when (this.programmeChecklistComponentEntity.type) {
        ProgrammeChecklistComponentType.HEADLINE -> this.metadata?.toHeadlineInstance()
        ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> this.metadata?.toOptionsToggleInstance()
        ProgrammeChecklistComponentType.TEXT_INPUT -> this.metadata?.toTextInputInstance()
        ProgrammeChecklistComponentType.SCORE -> this.metadata?.toScoreInstance()
    }

fun ChecklistInstanceEntity.update(checklist: ChecklistInstanceDetail) {
    this.status = checklist.status
    components?.forEach {
        it.metadata = findComponent(checklist, it.checklistComponentId.programmeComponentId)?.instanceMetadata.toJson()
    }
}

private fun findComponent(checklist: ChecklistInstanceDetail, id: Long): ChecklistComponentInstance? {
    return checklist.components?.find { it.id == id }
}
