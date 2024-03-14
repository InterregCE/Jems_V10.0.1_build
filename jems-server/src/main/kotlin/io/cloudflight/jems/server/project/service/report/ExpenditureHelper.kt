package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureVerification

fun List<ExpenditureVerification>.getTotalCertifiedPerLumpSum() =
    this.filter { it.lumpSumId != null }.groupBy { it.lumpSumId!! }
        .mapValues { it.value.sumOf { it.certifiedAmount  } }

fun List<ExpenditureVerification>.getTotalCertifiedPerUnitCost() =
    filter { it.unitCostId != null }
        .groupBy { it.unitCostId!! }
        .mapValues { it.value.sumOf { it.certifiedAmount } }

fun List<ExpenditureVerification>.getTotalCertifiedPerInvestment() =
    filter { it.investmentId != null }
        .groupBy { it.investmentId!! }
        .mapValues { it.value.sumOf { it.certifiedAmount } }

