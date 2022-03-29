package io.cloudflight.jems.api.programme.dto.checklist

import io.cloudflight.jems.api.programme.dto.checklist.metadata.ProgrammeChecklistMetadataDTO

data class ProgrammeChecklistComponentDTO(
    val id: Long?,
    val type: ProgrammeChecklistComponentTypeDTO,
    val position: Int,
    var metadata: ProgrammeChecklistMetadataDTO?
)
