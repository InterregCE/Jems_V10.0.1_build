package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.api.project.dto.InputTranslation

data class CorrectionAvailableFtls(
    val programmeLumpSumId: Long,
    val orderNr: Int,
    val name: Set<InputTranslation>,

    val availableFunds: List<CorrectionAvailableFund>,
)
