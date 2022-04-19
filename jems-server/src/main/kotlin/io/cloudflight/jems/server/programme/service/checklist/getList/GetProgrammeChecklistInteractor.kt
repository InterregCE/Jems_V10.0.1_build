package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType

interface GetProgrammeChecklistInteractor {

    fun getProgrammeChecklist(): List<ProgrammeChecklist>

    fun getProgrammeChecklistsByType(checklistType: ProgrammeChecklistType): List<IdNamePair>
}
