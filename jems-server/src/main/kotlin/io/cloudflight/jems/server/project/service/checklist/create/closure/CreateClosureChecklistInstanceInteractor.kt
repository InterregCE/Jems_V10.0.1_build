package io.cloudflight.jems.server.project.service.checklist.create.closure

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface CreateClosureChecklistInstanceInteractor {

    fun create(reportId: Long, programmeChecklistId: Long): ChecklistInstanceDetail
}
