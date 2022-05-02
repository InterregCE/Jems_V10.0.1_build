package io.cloudflight.jems.api.programme.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO

class TextInputMetadataDTO(
    var question: String = "",
    val explanationLabel: String = "",
    val explanationMaxLength: Int = 5000,
): ProgrammeChecklistMetadataDTO(ProgrammeChecklistComponentTypeDTO.TEXT_INPUT)
