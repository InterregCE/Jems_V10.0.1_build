package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanFinalizeReportVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportFinalizedVerification
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.calculateSourcesAndSplits
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.sumUp
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizeVerificationProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
    private val projectReportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence,
    private val getPartnerReportFinancialData: GetPartnerReportFinancialData,
) : FinalizeVerificationProjectReportInteractor {

    @Transactional
    @CanFinalizeReportVerification
    @ExceptionWrapper(FinalizeVerificationProjectReportException::class)
    override fun finalizeVerification(projectId: Long, reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportByIdUnSecured(reportId = reportId)
        validateReportIsInVerification(report)
        val parkedExpenditures =
            expenditureVerificationPersistence.getParkedProjectReportExpenditureVerification(reportId)

        val financialData = calculateSourcesAndSplits(
            verification = expenditureVerificationPersistence.getProjectReportExpenditureVerification(reportId),
            availableFunds = projectReportCertificateCoFinancingPersistence.getAvailableFunds(reportId),
            partnerReportFinancialDataResolver = { getPartnerReportFinancialData.retrievePartnerReportFinancialData(it) },
        )

        projectReportFinancialOverviewPersistence.storeOverviewPerFund(reportId, toStore = financialData)
        projectReportCertificateCoFinancingPersistence.updateAfterVerificationValues(
            report.projectId,
            reportId,
            financialData.sumUp().totalLineToColumn()
        )

        return reportPersistence.finalizeVerificationOnReportById(projectId = report.projectId, reportId = reportId)
            .also {
                auditPublisher.publishEvent(ProjectReportStatusChanged(this, it))
                auditPublisher.publishEvent(
                    projectReportFinalizedVerification(
                        context = this,
                        projectId = report.projectId,
                        report = it,
                        parkedExpenditures
                    )
                )
            }.status
    }

    private fun validateReportIsInVerification(report: ProjectReportModel) {
        if (report.status != ProjectReportStatus.InVerification)
            throw ReportVerificationNotStartedException()
    }

    private fun FinancingSourceBreakdownLine.totalLineToColumn() = ReportCertificateCoFinancingColumn(
        funds = fundsSorted.associate { Pair(it.first.id, it.second) }
            .plus(Pair(null, partnerContribution)),
        partnerContribution = partnerContribution,
        publicContribution = publicContribution,
        automaticPublicContribution = automaticPublicContribution,
        privateContribution = privateContribution,
        sum = total,
    )

}
