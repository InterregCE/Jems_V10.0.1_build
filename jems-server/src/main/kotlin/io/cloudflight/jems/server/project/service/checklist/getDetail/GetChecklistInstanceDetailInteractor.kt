package io.cloudflight.jems.server.programme.service.checklist.getDetail

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail

interface GetChecklistInstanceDetailInteractor {
    fun getChecklistInstanceDetail(id: Long): ChecklistInstanceDetail
}
