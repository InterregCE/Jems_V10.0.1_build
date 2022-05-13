package io.cloudflight.jems.server.project.service.checklist.create

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface CreateChecklistInstanceInteractor {
    fun create(createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail
}
