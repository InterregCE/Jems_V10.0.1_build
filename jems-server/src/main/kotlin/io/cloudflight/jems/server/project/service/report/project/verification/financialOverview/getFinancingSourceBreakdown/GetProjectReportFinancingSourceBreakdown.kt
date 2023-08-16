package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationFinance
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportFinancingSourceBreakdown(
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectReportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val projectReportVerificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence,
    private val projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence,
    private val getPartnerReportFinancialData: GetPartnerReportFinancialData,
    private val securityService: SecurityService,
) : GetProjectReportFinancingSourceBreakdownInteractor {

    @CanViewReportVerificationFinance
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportFinancingSourceBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): FinancingSourceBreakdown {

        val report = projectReportPersistence.getReportById(projectId = projectId, reportId)
        if (hasRestrictedViewForOngoingVerification(report)) {
            throw AccessDeniedException("Denied")
        }

        val reportIsFinalized = report.status == ProjectReportStatus.Finalized
        val sources = if (reportIsFinalized)
            projectReportFinancialOverviewPersistence.getOverviewPerFund(reportId)
        else
            calculateSourcesAndSplits(
                verification = projectReportVerificationExpenditurePersistence.getProjectReportExpenditureVerification(reportId),
                availableFunds = projectReportCertificateCoFinancingPersistence.getAvailableFunds(reportId),
                partnerReportFinancialDataResolver = { getPartnerReportFinancialData.retrievePartnerReportFinancialData(it) },
            )

        val total = if (reportIsFinalized)
            projectReportCertificateCoFinancingPersistence
                .getCoFinancing(projectId = projectId, reportId).currentVerified
                .toTotalLine(availableFunds = projectReportCertificateCoFinancingPersistence.getAvailableFunds(reportId))
        else
            sources.sumUp()

        return FinancingSourceBreakdown(
            sources = sources,
            total = total,
        )
    }

    private fun hasRestrictedViewForOngoingVerification(report: ProjectReportModel): Boolean {
        val hasGlobalProjectVerificationView = securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingProjectView)
        return !report.status.isFinalized() && hasGlobalProjectVerificationView == false
    }

}
