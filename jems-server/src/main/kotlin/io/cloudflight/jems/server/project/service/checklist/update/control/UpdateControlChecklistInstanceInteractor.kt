package io.cloudflight.jems.server.project.service.checklist.update.control

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus

interface UpdateControlChecklistInstanceInteractor {

    fun update(partnerId: Long, reportId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun changeStatus(partnerId: Long, reportId: Long, checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance
}