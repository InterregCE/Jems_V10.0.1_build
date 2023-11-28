package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.api.project.dto.InputTranslation

data class CorrectionAvailableFtls(
    val orderNr: Int,
    val programmeLumpSumId: Long,
    val name: Set<InputTranslation>,

    val availableFunds: List<CorrectionAvailableFund>,
)
