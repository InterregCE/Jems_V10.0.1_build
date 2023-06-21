package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost

data class ExpenditureValidationData(
    val allowedLumpSumsById: Map<Long, ProjectPartnerReportLumpSum>,
    val allowedUnitCostsById: Map<Long, ProjectPartnerReportUnitCost>,
    val allowedInvestmentIds: Set<Long>,
    val allowedProcurementIds: Set<Long>,
    val defaultCurrency: String?,
)
