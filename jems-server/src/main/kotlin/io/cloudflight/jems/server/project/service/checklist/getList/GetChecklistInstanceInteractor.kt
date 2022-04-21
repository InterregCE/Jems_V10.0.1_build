package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType

interface GetChecklistInstanceInteractor {

    fun getChecklistInstancesOfCurrentUserByTypeAndRelatedId(
        relatedToId: Long,
        type: ProgrammeChecklistType
    ): List<ChecklistInstance>
}
