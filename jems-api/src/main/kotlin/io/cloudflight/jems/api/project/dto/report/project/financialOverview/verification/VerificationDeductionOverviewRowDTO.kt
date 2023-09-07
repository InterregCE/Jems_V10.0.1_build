package io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification

import java.math.BigDecimal

class VerificationDeductionOverviewRowDTO(
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
    var total: BigDecimal
)
