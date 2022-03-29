package io.cloudflight.jems.server.programme.service.checklist.model

import java.time.ZonedDateTime

class ProgrammeChecklistDetail(
    id: Long?,
    type: ProgrammeChecklistType = ProgrammeChecklistType.ELIGIBILITY,
    name: String?,
    lastModificationDate: ZonedDateTime?,
    val components: List<ProgrammeChecklistComponent>? = emptyList()
) : ProgrammeChecklist(id, type, name, lastModificationDate)
