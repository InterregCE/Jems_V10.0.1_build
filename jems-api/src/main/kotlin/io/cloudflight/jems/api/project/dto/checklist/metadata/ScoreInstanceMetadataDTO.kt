package io.cloudflight.jems.api.project.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import java.math.BigDecimal

data class ScoreInstanceMetadataDTO(val score: BigDecimal, val justification: String) :
    ChecklistInstanceMetadataDTO(ProgrammeChecklistComponentTypeDTO.SCORE)
