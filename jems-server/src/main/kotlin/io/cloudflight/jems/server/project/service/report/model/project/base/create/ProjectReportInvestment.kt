package io.cloudflight.jems.server.project.service.report.model.project.base.create

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportInvestment(
    val investmentId: Long,
    val investmentNumber: Int,
    val workPackageNumber: Int,
    val title: Set<InputTranslation>,
    val deactivated: Boolean,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyVerified: BigDecimal,
)
