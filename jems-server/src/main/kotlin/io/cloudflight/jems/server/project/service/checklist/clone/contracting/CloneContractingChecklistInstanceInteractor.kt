package io.cloudflight.jems.server.project.service.checklist.clone.contracting

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface CloneContractingChecklistInstanceInteractor {
    fun clone(projectId: Long, checklistId: Long): ChecklistInstanceDetail
}
