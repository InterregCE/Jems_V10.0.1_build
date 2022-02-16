package io.cloudflight.jems.api.project.dto.report.partner

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal

data class PartnerReportIdentificationCoFinancingDTO(
    val fund: ProgrammeFundDTO,
    val percentage: BigDecimal,
)
