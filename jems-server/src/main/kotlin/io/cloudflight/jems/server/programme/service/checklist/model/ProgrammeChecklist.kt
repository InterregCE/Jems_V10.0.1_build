package io.cloudflight.jems.server.programme.service.checklist.model

import java.time.ZonedDateTime

open class ProgrammeChecklist(
    val id: Long?,
    val type: ProgrammeChecklistType = ProgrammeChecklistType.ELIGIBILITY,
    val name: String?,
    val lastModificationDate: ZonedDateTime?
)
