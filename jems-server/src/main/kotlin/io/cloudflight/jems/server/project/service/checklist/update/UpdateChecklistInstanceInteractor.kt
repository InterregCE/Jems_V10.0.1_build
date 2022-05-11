package io.cloudflight.jems.server.project.service.checklist.update

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus

interface UpdateChecklistInstanceInteractor {
    fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun changeStatus(checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance

    fun updateSelection(checklistId: Long, visible: Boolean)
}
