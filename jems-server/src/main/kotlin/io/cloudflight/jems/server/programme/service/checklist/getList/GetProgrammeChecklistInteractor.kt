package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import org.springframework.data.domain.Sort

interface GetProgrammeChecklistInteractor {

    fun getProgrammeChecklist(sort: Sort): List<ProgrammeChecklist>

    fun getProgrammeChecklistsByType(checklistType: ProgrammeChecklistType): List<IdNamePair>
}
