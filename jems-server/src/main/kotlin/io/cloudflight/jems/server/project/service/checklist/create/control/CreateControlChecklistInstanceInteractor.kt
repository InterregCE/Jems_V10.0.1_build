package io.cloudflight.jems.server.project.service.checklist.create.control

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface CreateControlChecklistInstanceInteractor {

    fun create(partnerId: Long, reportId: Long, createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail
}