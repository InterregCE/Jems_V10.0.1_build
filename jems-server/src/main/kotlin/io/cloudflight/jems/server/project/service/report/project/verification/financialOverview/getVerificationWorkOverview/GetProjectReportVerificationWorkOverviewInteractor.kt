package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview

interface GetProjectReportVerificationWorkOverviewInteractor {

    fun get(reportId: Long): VerificationWorkOverview

}
