package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail

interface UpdateProgrammeChecklistInteractor {
    fun update(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail
}
