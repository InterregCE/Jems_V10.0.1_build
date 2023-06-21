package io.cloudflight.jems.server.project.service.report.partner.base.canCreateProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary

interface CanCreateProjectPartnerReportInteractor {
    fun canCreateReportFor(partnerId: Long): Boolean
}
