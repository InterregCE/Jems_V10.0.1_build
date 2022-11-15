package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectPartnerReportInvestment(
    val id: Long,
    val investmentId: Long,
    val workPackageNumber: Int,
    val investmentNumber: Int,
    val title: Set<InputTranslation>,
    val total: BigDecimal,
)
