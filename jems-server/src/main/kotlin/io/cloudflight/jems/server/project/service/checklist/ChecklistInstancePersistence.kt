package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface ChecklistInstancePersistence {

    fun getChecklistsByRelationAndCreatorAndType(
        relatedToId: Long,
        creatorId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>

    fun getChecklistDetail(id: Long): ChecklistInstanceDetail

    fun create(createChecklist: CreateChecklistInstanceModel, creatorId: Long): ChecklistInstanceDetail

    fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun deleteById(id: Long)

    fun getStatus(id: Long): ChecklistInstanceStatus

    fun countAllByChecklistTemplateId(checklistTemplateId: Long): Long
}
