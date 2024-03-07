package io.cloudflight.jems.server.project.service.checklist.getInstances.closure

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance

interface GetClosureChecklistInstancesInteractor {

    fun getClosureChecklistInstances(projectId: Long, reportId: Long): List<ChecklistInstance>
}
