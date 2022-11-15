package io.cloudflight.jems.server.project.service.report.model.partner

import java.math.BigDecimal

data class PartnerReportIdentificationCoFinancing(
    val fundId: Long,
    val percentage: BigDecimal,
)
