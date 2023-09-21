package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentRegularToCreate
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanFinalizeReportVerification
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory.ProjectReportCertificateCostCategoryPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerCertificateFundSplit
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureIdentifiers
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportFinalizedVerification
import io.cloudflight.jems.server.project.service.report.project.verification.calculateCostCategoriesCurrentVerified
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.calculateSourcesAndSplits
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.sumUp
import io.cloudflight.jems.server.project.service.report.project.verification.toIdentifiers
import io.cloudflight.jems.server.project.service.report.project.verification.toVerification
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
class FinalizeVerificationProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
    private val projectReportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val projectReportCertificateCostCategoryPersistenceProvider: ProjectReportCertificateCostCategoryPersistenceProvider,
    private val projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence,
    private val getPartnerReportFinancialData: GetPartnerReportFinancialData,
    private val paymentRegularPersistence: PaymentPersistence,
    private val partnerReportCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence,
    private val reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectReportCertificateInvestmentPersistence,
) : FinalizeVerificationProjectReportInteractor {

    @Transactional
    @CanFinalizeReportVerification
    @ExceptionWrapper(FinalizeVerificationProjectReportException::class)
    override fun finalizeVerification(reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportByIdUnSecured(reportId = reportId)
        validateReportIsInVerification(report)
        val projectReportVerificationExpenditures = expenditureVerificationPersistence.getProjectReportExpenditureVerification(reportId)

        val financialData = calculateSourcesAndSplits(
            verification = projectReportVerificationExpenditures,
            availableFundsResolver = { certificateId -> partnerReportCoFinancingPersistence.getAvailableFunds(certificateId) },
            partnerReportFinancialDataResolver = { getPartnerReportFinancialData.retrievePartnerReportFinancialData(it) },
        )

        val reportPartnerCertificateSplits = projectReportFinancialOverviewPersistence.storeOverviewPerFund(reportId, toStore = financialData)
        projectReportCertificateCoFinancingPersistence.updateAfterVerificationValues(
            projectId = report.projectId,
            reportId = reportId,
            afterVerification = financialData.sumUp().totalLineToColumn()
        )


        val expendituresByCertificate = projectReportVerificationExpenditures.groupBy({ it.toIdentifiers() }, { it.toVerification() })
        val budgetOptionsByCertificate =
            reportExpenditureCostCategoryPersistence.getCostCategoriesFor(expendituresByCertificate.keys.mapTo(HashSet()) { it.partnerReportId })
                .mapValues { it.value.options }

        projectReportCertificateCostCategoryPersistenceProvider.updateAfterVerification(
            projectId = report.projectId,
            reportId = reportId,
            currentVerified = expendituresByCertificate.calculateCostCategoriesCurrentVerified(budgetOptionsByCertificate)
        )

        reportCertificateLumpSumPersistence.updateCurrentlyVerifiedValues(
            projectId = report.projectId,
            reportId = reportId,
            verifiedValues = expendituresByCertificate.getAfterVerificationForLumpSums()
        )

        reportCertificateUnitCostPersistence.updateCurrentlyVerifiedValues(
            projectId = report.projectId,
            reportId = reportId,
            verifiedValues = expendituresByCertificate.getAfterVerificationForUnitCosts()
        )

        reportInvestmentPersistence.updateCurrentlyVerifiedValues(
            projectId = report.projectId,
            reportId = reportId,
            verifiedValues = expendituresByCertificate.getAfterVerificationForInvestments()
        )

        val paymentsToSave = createPaymentsForReport(reportPartnerCertificateSplits, report)
        paymentRegularPersistence.saveRegularPayments(projectReportId = reportId, paymentsToSave)

        return reportPersistence.finalizeVerificationOnReportById(projectId = report.projectId, reportId, ZonedDateTime.now()).also {
            auditPublisher.publishEvent(ProjectReportStatusChanged(this, it))
            auditPublisher.publishEvent(
                projectReportFinalizedVerification(
                    context = this,
                    projectId = report.projectId,
                    report = it,
                    parkedExpenditures = expenditureVerificationPersistence.getParkedProjectReportExpenditureVerification(reportId),
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

    private fun createPaymentsForReport(
        certificateSplits: List<PartnerCertificateFundSplit>,
        projectReport: ProjectReportModel,
    ): List<PaymentRegularToCreate> =
        certificateSplits.groupBy { it.fundId }
            .map { (fundId, certificateFundSplits) ->
                PaymentRegularToCreate(
                    projectId = projectReport.projectId,
                    fundId = fundId,
                    amountApprovedPerFund = certificateFundSplits.getTotalPaymentForFund(),
                    partnerPayments = certificateFundSplits.map {
                        PaymentPartnerToCreate(
                            partnerId = it.partnerId,
                            partnerReportId = it.partnerReportId,
                            amountApprovedPerPartner = it.value
                        )
                    }
                )
            }


    private fun List<PartnerCertificateFundSplit>.getTotalPaymentForFund() = this.sumOf { it.value }


    private fun Map<ExpenditureIdentifiers, List<ExpenditureVerification>>.getAfterVerificationForLumpSums(): Map<Long, BigDecimal> {
        return this.values.flatten().filter { it.lumpSumId != null }.groupBy { it.lumpSumId!! }
            .mapValues { it.value.sumOf { expenditure -> expenditure.amountAfterVerification } }
    }

    private fun Map<ExpenditureIdentifiers, List<ExpenditureVerification>>.getAfterVerificationForUnitCosts(): Map<Long, BigDecimal> {
        return this.values.flatten().filter { it.unitCostId != null }.groupBy { it.unitCostId!! }
            .mapValues { it.value.sumOf { expenditure -> expenditure.amountAfterVerification } }
    }

    private fun Map<ExpenditureIdentifiers, List<ExpenditureVerification>>.getAfterVerificationForInvestments(): Map<Long, BigDecimal> {
        return this.values.flatten().filter { it.investmentId != null }.groupBy { it.investmentId!! }
            .mapValues { it.value.sumOf { expenditure -> expenditure.amountAfterVerification } }
    }


}
