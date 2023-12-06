package io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.plusSpf
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetPerPartnerCostCategoryBreakdown(
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
): GetPerPartnerCostCategoryBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPerPartnerCostCategoryBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): PerPartnerCostCategoryBreakdown {
        val data = reportCertificateCostCategoryPersistence
            .getCostCategoriesPerPartner(projectId, reportId = reportId)
            .toMutableList()

        // SPF is not reported in partner reports, so we need to add its current value extra
        val spfCurrent = reportSpfClaimPersistence.getCurrentSpfContribution(reportId).sum
        data.addSpf(spfAmount = spfCurrent)

        return PerPartnerCostCategoryBreakdown(
            partners = data.sortedBy { it.partnerNumber },
            totalCurrent = data.sumOf { it.current },
            totalDeduction = data.sumOf { it.deduction },
        )
    }

    private fun MutableList<PerPartnerCostCategoryBreakdownLine>.addSpf(spfAmount: BigDecimal) {
        if (size != 1)
            return

        val partnerCurrent = first().current
        this[0] = first().copy(current = partnerCurrent.plusSpf(spfAmount))
    }

}
