package io.cloudflight.jems.server.project.service.checklist.getInstances.verification

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance

interface GetVerificationChecklistsInstancesInteractor {

    fun getVerificationChecklistInstances(
        projectId: Long,
        reportId: Long
    ): List<ChecklistInstance>
}