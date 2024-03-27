package io.cloudflight.jems.server.project.service.checklist.getDetail.closure

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface GetClosureChecklistInstanceDetailInteractor {

    fun getClosureChecklistInstanceDetail(projectId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail
}
