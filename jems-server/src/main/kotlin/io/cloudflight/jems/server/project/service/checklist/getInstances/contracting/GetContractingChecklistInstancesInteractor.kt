package io.cloudflight.jems.server.project.service.checklist.getInstances.contracting

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance

interface GetContractingChecklistInstancesInteractor {

    fun getContractingChecklistInstances(
        projectId: Long
    ): List<ChecklistInstance>
}
