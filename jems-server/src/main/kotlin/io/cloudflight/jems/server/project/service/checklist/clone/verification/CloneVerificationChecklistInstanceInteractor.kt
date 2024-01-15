package io.cloudflight.jems.server.project.service.checklist.clone.verification

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface CloneVerificationChecklistInstanceInteractor {
    fun clone(projectId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail
}
