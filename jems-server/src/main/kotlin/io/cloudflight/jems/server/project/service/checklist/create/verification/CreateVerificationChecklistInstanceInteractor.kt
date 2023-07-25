package io.cloudflight.jems.server.project.service.checklist.create.verification

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface CreateVerificationChecklistInstanceInteractor {

    fun create(projectId: Long, reportId: Long, createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail
}