package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist

interface GetProgrammeChecklistInteractor {

    fun getProgrammeChecklist(): List<ProgrammeChecklist>
}
