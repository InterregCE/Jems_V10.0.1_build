package io.cloudflight.jems.server.programme.service.checklist.model

import java.time.ZonedDateTime

interface ProgrammeChecklistRow {
    val id: Long?
    val type: ProgrammeChecklistType
    val name: String?
    val lastModificationDate: ZonedDateTime?
    val count: Int
}
