package io.cloudflight.jems.server.project.service.checklist.getInstances

import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType

interface GetChecklistInstancesInteractor {

    fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>

    fun getChecklistInstancesByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>

    fun getChecklistInstancesForSelection(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>
}
