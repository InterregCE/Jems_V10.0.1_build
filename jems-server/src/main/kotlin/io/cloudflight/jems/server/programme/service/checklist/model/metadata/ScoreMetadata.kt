package io.cloudflight.jems.server.programme.service.checklist.model.metadata

import java.math.BigDecimal

class ScoreMetadata (
    var question: String = "",
    val weight: BigDecimal = BigDecimal.ONE,
): ProgrammeChecklistMetadata
