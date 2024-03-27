package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import java.time.ZonedDateTime

data class CallChecklist(
    val id: Long? = null,
    val name: String?,
    val type: ProgrammeChecklistType,
    val lastModificationDate: ZonedDateTime?,
    val selected: Boolean
)
