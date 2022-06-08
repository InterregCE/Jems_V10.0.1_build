package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.project.service.checklist.model.*

interface ChecklistInstancePersistence {

    fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance>

    fun getChecklistDetail(id: Long): ChecklistInstanceDetail

    fun getChecklistSummary(checklistId: Long): ChecklistInstance

    fun create(createChecklist: CreateChecklistInstanceModel, creatorId: Long): ChecklistInstanceDetail

    fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun updateSelection(selection: Map<Long,  Boolean>): List<ChecklistInstance>

    fun deleteById(id: Long)

    fun countAllByChecklistTemplateId(checklistTemplateId: Long): Long

    fun consolidateChecklistInstance(checklistId: Long, consolidated: Boolean): ChecklistInstance

    fun changeStatus(checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance
}
