package io.cloudflight.jems.server.project.service.report.project.verification.expenditure

import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased

interface ProjectReportVerificationExpenditurePersistence {

    fun getProjectReportExpenditureVerification(
        projectReportId: Long
    ): List<ProjectReportVerificationExpenditureLine>

    fun getExpenditureVerificationRiskBasedData(
        projectId: Long,
        projectReportId: Long
    ): ProjectReportVerificationRiskBased

    fun getParkedProjectReportExpenditureVerification(
        projectReportId: Long
    ): List<ProjectReportVerificationExpenditureLine>

    fun initiateEmptyVerificationForProjectReport(projectReportId: Long)

    fun updateProjectReportExpenditureVerification(
        projectReportId: Long,
        expenditureVerification: List<ProjectReportVerificationExpenditureLineUpdate>
    ): List<ProjectReportVerificationExpenditureLine>

    fun updateProjectReportExpenditureVerificationRiskBased(
        projectId: Long,
        projectReportId: Long,
        riskBasedData: ProjectReportVerificationRiskBased
    ): ProjectReportVerificationRiskBased
}
