package io.cloudflight.jems.api.project.dto.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportInvestmentDTO(
    val id: Long,
    val investmentId: Long,
    val workPackageNumber: Int,
    val investmentNumber: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
)
