package io.cloudflight.jems.api.project.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO

data class TextInputInstanceMetadataDTO(val explanation: String) :
    ChecklistInstanceMetadataDTO(ProgrammeChecklistComponentTypeDTO.TEXT_INPUT)
