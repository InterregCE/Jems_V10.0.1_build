package io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts

import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost

interface PartnerReportExpenditureCostsInteractor {

    fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<PartnerReportExpenditureCost>
    ): List<PartnerReportExpenditureCost>

    fun getPartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long
    ): List<PartnerReportExpenditureCost>
}
