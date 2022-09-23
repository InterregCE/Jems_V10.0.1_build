package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface ControlChecklistInstancePersistence {

    fun create(createChecklist: CreateChecklistInstanceModel, creatorId: Long, reportId: Long): ChecklistInstanceDetail

    fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance>
}