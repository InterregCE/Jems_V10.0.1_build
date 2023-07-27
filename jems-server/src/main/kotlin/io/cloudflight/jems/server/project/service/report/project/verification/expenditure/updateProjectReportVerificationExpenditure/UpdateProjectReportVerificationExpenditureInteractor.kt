package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditure

import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine

interface UpdateProjectReportVerificationExpenditureInteractor {

    fun updateExpenditureVerification(
        projectReportId: Long,
        expenditureVerificationUpdate: List<ProjectReportVerificationExpenditureLineUpdate>
    ): List<ProjectReportVerificationExpenditureLine>
}
