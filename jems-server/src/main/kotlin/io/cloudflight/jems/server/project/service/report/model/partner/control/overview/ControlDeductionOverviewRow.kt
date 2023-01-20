package io.cloudflight.jems.server.project.service.report.model.partner.control.overview

import java.math.BigDecimal

data class ControlDeductionOverviewRow(
    val typologyOfErrorId: Long?,
    val typologyOfErrorName: String?,
    val staffCost: BigDecimal?,
    val officeAndAdministration: BigDecimal?,
    val travelAndAccommodation: BigDecimal?,
    val externalExpertise: BigDecimal?,
    val equipment: BigDecimal?,
    val infrastructureAndWorks: BigDecimal?,
    val lumpSums: BigDecimal?,
    val unitCosts: BigDecimal?,
    val otherCosts: BigDecimal?,
    var total: BigDecimal = BigDecimal.ZERO
)
