package io.cloudflight.jems.server.project.service.report.model.project.identification

import java.math.BigDecimal

data class ProjectReportSpendingProfileReportedValues(
    val partnerId: Long,
    val previouslyReported: BigDecimal,
    var currentlyReported: BigDecimal,
    val partnerTotalEligibleBudget: BigDecimal
)
