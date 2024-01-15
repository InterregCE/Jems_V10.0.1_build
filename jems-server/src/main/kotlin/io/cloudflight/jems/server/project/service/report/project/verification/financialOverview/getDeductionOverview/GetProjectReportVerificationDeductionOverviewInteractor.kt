package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.CertificateVerificationDeductionOverview

interface GetProjectReportVerificationDeductionOverviewInteractor {

    fun getDeductionOverview(reportId: Long): List<CertificateVerificationDeductionOverview>
}