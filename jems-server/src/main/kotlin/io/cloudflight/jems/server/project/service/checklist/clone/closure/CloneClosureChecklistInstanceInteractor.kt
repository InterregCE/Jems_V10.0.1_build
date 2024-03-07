package io.cloudflight.jems.server.project.service.checklist.clone.closure

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface CloneClosureChecklistInstanceInteractor {

    fun clone(reportId: Long, checklistId: Long): ChecklistInstanceDetail
}
