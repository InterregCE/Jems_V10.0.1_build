package io.cloudflight.jems.api.project.dto.report.partner.control.overview

import java.math.BigDecimal

data class ControlDeductionOverviewRowDTO(
    val typologyOfErrorId: Long?,
    val typologyOfErrorName: String?,
    val staffCost: BigDecimal?,
    var officeAndAdministration: BigDecimal?,
    var travelAndAccommodation: BigDecimal?,
    val externalExpertise: BigDecimal?,
    val equipment: BigDecimal?,
    val infrastructureAndWorks: BigDecimal?,
    val lumpSums: BigDecimal?,
    var unitCosts: BigDecimal?,
    var otherCosts: BigDecimal?,
    val total: BigDecimal = BigDecimal.ZERO
)
