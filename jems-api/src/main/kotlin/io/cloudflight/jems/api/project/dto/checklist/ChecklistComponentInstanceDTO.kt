package io.cloudflight.jems.api.project.dto.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.ProgrammeChecklistMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.ChecklistInstanceMetadataDTO

data class ChecklistComponentInstanceDTO(
    val id: Long,
    val type: ProgrammeChecklistComponentTypeDTO,
    val position: Int,
    var programmeMetadata: ProgrammeChecklistMetadataDTO?,
    var instanceMetadata: ChecklistInstanceMetadataDTO?
)
