package io.cloudflight.jems.server.programme.service.checklist.create

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail

interface CreateProgrammeChecklistInteractor {
    fun create(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail
}
