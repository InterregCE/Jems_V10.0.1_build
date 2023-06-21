package io.cloudflight.jems.server.project.service.checklist.update.contracting

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus

interface UpdateContractingChecklistInstanceInteractor {

    fun update(projectId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail

    fun changeStatus(projectId: Long, checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance

    fun updateContractingChecklistDescription(projectId: Long, checklistId: Long, description: String?): ChecklistInstance
}
