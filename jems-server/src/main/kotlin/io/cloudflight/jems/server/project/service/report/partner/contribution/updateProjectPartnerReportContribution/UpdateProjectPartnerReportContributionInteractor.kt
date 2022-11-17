package io.cloudflight.jems.server.project.service.report.partner.contribution.updateProjectPartnerReportContribution

import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.update.UpdateProjectPartnerReportContributionWrapper

interface UpdateProjectPartnerReportContributionInteractor {

    fun update(partnerId: Long, reportId: Long, data: UpdateProjectPartnerReportContributionWrapper): ProjectPartnerReportContributionData

}
