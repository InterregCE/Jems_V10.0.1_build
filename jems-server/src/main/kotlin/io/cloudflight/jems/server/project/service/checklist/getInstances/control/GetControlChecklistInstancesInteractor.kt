package io.cloudflight.jems.server.project.service.checklist.getInstances.control

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance

interface GetControlChecklistInstancesInteractor {

    fun getControlChecklistInstances(
        partnerId: Long,
        reportId: Long
    ): List<ChecklistInstance>
}