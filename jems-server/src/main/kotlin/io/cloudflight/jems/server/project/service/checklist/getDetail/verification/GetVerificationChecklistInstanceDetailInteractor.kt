package io.cloudflight.jems.server.project.service.checklist.getDetail.verification

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface GetVerificationChecklistInstanceDetailInteractor {

    fun getVerificationChecklistInstanceDetail(projectId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail
}