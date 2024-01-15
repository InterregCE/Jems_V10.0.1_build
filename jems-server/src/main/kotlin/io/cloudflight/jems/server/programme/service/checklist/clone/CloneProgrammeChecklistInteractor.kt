package io.cloudflight.jems.server.programme.service.checklist.clone

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail

interface CloneProgrammeChecklistInteractor {

    fun clone(checklistId: Long): ProgrammeChecklistDetail
}
