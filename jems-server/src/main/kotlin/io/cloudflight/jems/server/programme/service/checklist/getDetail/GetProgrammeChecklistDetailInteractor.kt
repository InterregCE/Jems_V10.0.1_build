package io.cloudflight.jems.server.programme.service.checklist.getDetail

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail

interface GetProgrammeChecklistDetailInteractor {
    fun getProgrammeChecklistDetail(id: Long): ProgrammeChecklistDetail
}
