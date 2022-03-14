package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.report.PartnerReportExpenditureCostsApi
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts.PartnerReportExpenditureCostsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class PartnerReportExpenditureCostsController(
    private val partnerReportExpenditureCostsInteractor: PartnerReportExpenditureCostsInteractor,
) : PartnerReportExpenditureCostsApi {

    override fun getProjectPartnerReports(
        partnerId: Long, reportId: Long
    ): List<PartnerReportExpenditureCostDTO> =
        partnerReportExpenditureCostsInteractor.getPartnerReportExpenditureCosts(
            partnerId, reportId
        ).toDto()

    override fun updatePartnerReportExpenditures(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<PartnerReportExpenditureCostDTO>
    ): List<PartnerReportExpenditureCostDTO> =
        partnerReportExpenditureCostsInteractor.updatePartnerReportExpenditureCosts(
            partnerId,
            reportId,
            expenditureCosts.toModel()
        ).toDto()
}
