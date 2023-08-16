package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationFinance
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportFinancingSourceBreakdown(
    private val projectReportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService,
    private val calculator: GetProjectReportFinancingSourceBreakdownCalculator
) : GetProjectReportFinancingSourceBreakdownInteractor {

    @CanViewReportVerificationFinance
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportFinancingSourceBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): FinancingSourceBreakdown {
        val report = projectReportPersistence.getReportById(projectId = projectId, reportId)

        if (hasRestrictedViewForOngoingVerification(report.status.isFinalized())) {
            throw ProjectReportVerificationOverviewRestricted()
        }

        return calculator.getFinancingSource(projectId = projectId, reportId = reportId)
    }

    private fun hasRestrictedViewForOngoingVerification(reportIsFinalized: Boolean): Boolean {
        val hasGlobalProjectVerificationView = securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingProjectView) ?: false
        return !reportIsFinalized && !hasGlobalProjectVerificationView
    }

}
