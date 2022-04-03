package io.cloudflight.jems.api.programme.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO

data class HeadlineMetadataDTO(var value: String?) :
    ProgrammeChecklistMetadataDTO(ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE)
