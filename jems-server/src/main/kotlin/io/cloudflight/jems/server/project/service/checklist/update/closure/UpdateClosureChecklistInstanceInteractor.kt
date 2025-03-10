package io.cloudflight.jems.server.project.service.checklist.update.closure

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus

interface UpdateClosureChecklistInstanceInteractor {

    fun update(reportId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun changeStatus(reportId: Long, checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance

    fun updateDescription(reportId: Long, checklistId: Long, description: String?): ChecklistInstance
}
