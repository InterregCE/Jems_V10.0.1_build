package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class FinancingSourceFundDTO(
    val id: Long,
    val type: ProgrammeFundTypeDTO = ProgrammeFundTypeDTO.OTHER,
    val abbreviation: Set<InputTranslation> = emptySet(),
    val amount: BigDecimal,
)
