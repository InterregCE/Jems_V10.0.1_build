package io.cloudflight.jems.server.project.service.checklist.clone

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface CloneChecklistInstanceInteractor {
    fun clone(checklistId: Long): ChecklistInstanceDetail
}
