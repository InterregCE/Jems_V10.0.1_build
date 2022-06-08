package io.cloudflight.jems.api.programme.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO

data class OptionsToggleMetadataDTO(
    var question: String = "",
    val firstOption: String,
    val secondOption: String,
    val thirdOption: String? = null,
    val justification: String? = null
) : ProgrammeChecklistMetadataDTO(ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE)
