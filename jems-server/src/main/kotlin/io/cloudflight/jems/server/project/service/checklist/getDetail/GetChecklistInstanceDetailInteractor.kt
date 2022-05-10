package io.cloudflight.jems.server.project.service.checklist.getDetail

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface GetChecklistInstanceDetailInteractor {
    fun getChecklistInstanceDetail(id: Long): ChecklistInstanceDetail
}
