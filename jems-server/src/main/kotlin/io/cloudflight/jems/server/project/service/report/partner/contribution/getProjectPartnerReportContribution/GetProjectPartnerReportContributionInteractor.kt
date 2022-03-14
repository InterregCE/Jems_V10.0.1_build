package io.cloudflight.jems.server.project.service.report.partner.contribution.getProjectPartnerReportContribution

import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData

interface GetProjectPartnerReportContributionInteractor {

    fun getContribution(partnerId: Long, reportId: Long): ProjectPartnerReportContributionData

}
