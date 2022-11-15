package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class PartnerReportInvestmentSummary(
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val title: Set<InputTranslation>,
)
