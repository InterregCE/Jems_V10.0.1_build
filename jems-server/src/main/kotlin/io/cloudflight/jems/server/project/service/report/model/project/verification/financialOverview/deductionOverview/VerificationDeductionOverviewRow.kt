package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview

import java.math.BigDecimal

data class VerificationDeductionOverviewRow(
    val typologyOfErrorId: Long?,
    val typologyOfErrorName: String?,
    val staffCost: BigDecimal,
    var officeAndAdministration: BigDecimal,
    var travelAndAccommodation: BigDecimal,
    val externalExpertise: BigDecimal,
    val equipment: BigDecimal,
    val infrastructureAndWorks: BigDecimal,
    val lumpSums: BigDecimal,
    var unitCosts: BigDecimal,
    var otherCosts: BigDecimal,
    val total: BigDecimal
)
