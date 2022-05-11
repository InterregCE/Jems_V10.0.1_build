package io.cloudflight.jems.server.project.service.checklist.getMyInstances

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType

interface GetMyChecklistInstancesInteractor {

    fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>

    fun getChecklistInstancesForSelection(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>
}
