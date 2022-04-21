package io.cloudflight.jems.server.project.repository.checklist

import io.cloudflight.jems.server.common.gson.toHeadlineInstance
import io.cloudflight.jems.server.common.gson.toJson
import io.cloudflight.jems.server.common.gson.toOptionsToggleInstance
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import io.cloudflight.jems.server.programme.repository.checklist.toModelMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ChecklistInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceEntity
import io.cloudflight.jems.server.project.entity.checklist.ChecklistComponentInstanceId
import io.cloudflight.jems.server.project.entity.checklist.ChecklistInstanceEntity

fun ChecklistInstanceEntity.toDto(): ChecklistInstance =
    ChecklistInstance(
        id,
        status,
        programmeChecklist.type,
        programmeChecklist.name,
        creator.email,
        relatedToId,
        finishedDate,
        programmeChecklist.id
    )

fun List<ChecklistInstanceEntity>.toDto(): List<ChecklistInstance> = this.map { it.toDto() }

fun ChecklistInstanceEntity.toDetailModel(): ChecklistInstanceDetail {
    return ChecklistInstanceDetail(
        id = id,
        programmeChecklistId = programmeChecklist.id,
        status = status,
        type = programmeChecklist.type,
        name = programmeChecklist.name,
        finishedDate = finishedDate,
        relatedToId = relatedToId,
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
