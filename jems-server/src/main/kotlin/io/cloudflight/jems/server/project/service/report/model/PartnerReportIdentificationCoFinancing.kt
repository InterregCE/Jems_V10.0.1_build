package io.cloudflight.jems.server.project.service.report.model

import java.math.BigDecimal

data class PartnerReportIdentificationCoFinancing(
    val fundId: Long,
    val percentage: BigDecimal,
)
