package io.cloudflight.jems.server.project.service.checklist.clone.control

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface CloneControlChecklistInstanceInteractor {
    fun clone(partnerId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail
}
