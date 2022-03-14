package io.cloudflight.jems.server.project.service.report.partner.contribution.getProjectPartnerReportContribution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.toModelData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportContribution(
    private val reportContributionPersistence: ProjectReportContributionPersistence,
) : GetProjectPartnerReportContributionInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportContributionException::class)
    override fun getContribution(partnerId: Long, reportId: Long): ProjectPartnerReportContributionData =
        reportContributionPersistence
            .getPartnerReportContribution(partnerId, reportId = reportId)
            .toModelData()

}
