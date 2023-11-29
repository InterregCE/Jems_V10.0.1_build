package io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData

import io.cloudflight.jems.api.project.dto.InputTranslation

data class CorrectionAvailableFtlsDTO(
    val orderNr: Int,
    val programmeLumpSumId: Long,
    val name: Set<InputTranslation>,

    val availableFunds: List<CorrectionAvailableFundDTO>,
)
