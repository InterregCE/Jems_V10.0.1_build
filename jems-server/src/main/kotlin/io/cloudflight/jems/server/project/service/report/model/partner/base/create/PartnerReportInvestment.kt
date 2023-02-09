package io.cloudflight.jems.server.project.service.report.model.partner.base.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class PartnerReportInvestment(
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val title: Set<InputTranslation>,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyReportedParked: BigDecimal
)
