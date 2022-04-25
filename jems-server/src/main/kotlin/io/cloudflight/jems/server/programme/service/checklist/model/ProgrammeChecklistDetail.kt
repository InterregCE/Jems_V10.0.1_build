package io.cloudflight.jems.server.programme.service.checklist.model

import java.time.ZonedDateTime

class ProgrammeChecklistDetail(
    id: Long?,
    type: ProgrammeChecklistType = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
    name: String?,
    lastModificationDate: ZonedDateTime?,
    locked: Boolean,
    val components: List<ProgrammeChecklistComponent>? = emptyList()
) : ProgrammeChecklist(id, type, name, lastModificationDate, locked)
