package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod

interface GetProjectPartnerReportAvailablePeriodsInteractor {

    fun get(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportPeriod>

}
