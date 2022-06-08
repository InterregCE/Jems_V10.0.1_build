package io.cloudflight.jems.api.programme.dto.checklist.metadata

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import java.math.BigDecimal

class ScoreMetadataDTO(
    var question: String = "",
    var weight: BigDecimal = BigDecimal.ONE,
): ProgrammeChecklistMetadataDTO(ProgrammeChecklistComponentTypeDTO.SCORE)
