package io.cloudflight.jems.server.project.service.checklist.getDetail.control

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail

interface GetControlChecklistInstanceDetailInteractor {

    fun getControlChecklistInstanceDetail(partnerId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail
}