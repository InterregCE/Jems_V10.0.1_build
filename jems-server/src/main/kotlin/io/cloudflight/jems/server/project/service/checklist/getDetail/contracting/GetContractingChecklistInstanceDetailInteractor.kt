package io.cloudflight.jems.server.project.service.checklist.getDetail.contracting

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface GetContractingChecklistInstanceDetailInteractor {

    fun getContractingChecklistInstanceDetail(projectId: Long, checklistId: Long): ChecklistInstanceDetail
}
