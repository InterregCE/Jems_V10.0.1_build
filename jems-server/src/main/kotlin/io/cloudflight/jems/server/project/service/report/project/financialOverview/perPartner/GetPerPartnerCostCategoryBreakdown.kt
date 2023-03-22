package io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPerPartnerCostCategoryBreakdown(
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
): GetPerPartnerCostCategoryBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPerPartnerCostCategoryBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): PerPartnerCostCategoryBreakdown {
        val data = reportCertificateCostCategoryPersistence.getCostCategoriesPerPartner(projectId, reportId = reportId)

        return PerPartnerCostCategoryBreakdown(
            partners = data.sortedBy { it.partnerNumber },
            totalCurrent = data.sumOf { it.current },
            totalAfterControl = data.sumOf { it.afterControl },
        )
    }

}
