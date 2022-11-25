package io.cloudflight.jems.server.project.service.checklist.create.contracting

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel

interface CreateContractingChecklistInstanceInteractor {

    fun create(projectId: Long, createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail
}
