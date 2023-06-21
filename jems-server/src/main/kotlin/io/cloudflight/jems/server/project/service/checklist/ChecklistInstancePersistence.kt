package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface ChecklistInstancePersistence {

    fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance>

    fun getChecklistDetail(id: Long): ChecklistInstanceDetail

    fun getChecklistDetail(id: Long, type: ProgrammeChecklistType, relatedToId: Long): ChecklistInstanceDetail

    fun getChecklistSummary(checklistId: Long): ChecklistInstance

    fun getChecklistSummary(checklistId: Long, type: ProgrammeChecklistType, relatedToId: Long): ChecklistInstance

    fun create(createChecklist: CreateChecklistInstanceModel, creatorId: Long): ChecklistInstanceDetail

    fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun updateSelection(selection: Map<Long, Boolean>): List<ChecklistInstance>

    fun updateDescription(id: Long, description: String?): ChecklistInstance

    fun deleteById(id: Long)

    fun countAllByChecklistTemplateId(checklistTemplateId: Long): Long

    fun consolidateChecklistInstance(checklistId: Long, consolidated: Boolean): ChecklistInstance

    fun changeStatus(checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance

    fun existsByIdAndRelatedToId(id: Long, relatedToId: Long): Boolean
}
