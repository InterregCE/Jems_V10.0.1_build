package io.cloudflight.jems.api.project.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO

data class OptionsToggleInstanceMetadataDTO(
    var answer: String? = null,
    var justification: String? = null
) : ChecklistInstanceMetadataDTO(ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE)
