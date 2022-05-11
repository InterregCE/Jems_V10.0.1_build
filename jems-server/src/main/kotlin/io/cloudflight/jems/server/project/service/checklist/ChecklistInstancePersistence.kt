package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface ChecklistInstancePersistence {

    fun getChecklistsByRelationAndCreatorAndType(
        relatedToId: Long,
        creatorId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>

    fun getChecklistsByRelatedIdAndType(relatedToId: Long, type: ProgrammeChecklistType): List<ChecklistInstance>

    fun getChecklistsByRelationAndType(
        relatedToId: Long,
        type: ProgrammeChecklistType,
        visible: Boolean
    ): List<ChecklistInstance>

    fun getChecklistDetail(id: Long): ChecklistInstanceDetail

    fun getChecklistSummary(checklistId: Long): ChecklistInstance

    fun create(createChecklist: CreateChecklistInstanceModel, creatorId: Long): ChecklistInstanceDetail

    fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun updateSelection(checklistId: Long, visible: Boolean): ChecklistInstance

    fun deleteById(id: Long)

    fun countAllByChecklistTemplateId(checklistTemplateId: Long): Long

    fun consolidateChecklistInstance(checklistId: Long, consolidated: Boolean): ChecklistInstance

    fun changeStatus(checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance
}
