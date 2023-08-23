package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class ExpenditureIdentifiers(
    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,

    val partnerReportId: Long,
    val partnerReportNumber: Int,
)
