package io.cloudflight.jems.server.project.service.checklist.getAllInstances

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType

interface GetAllChecklistInstancesInteractor {

    fun getChecklistInstancesByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>
}
