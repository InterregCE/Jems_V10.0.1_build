package io.cloudflight.jems.server.programme.service.checklist.model

import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata

data class ProgrammeChecklistComponent(
    val id: Long?,
    val type: ProgrammeChecklistComponentType,
    val position: Int,
    var metadata: ProgrammeChecklistMetadata?
)
