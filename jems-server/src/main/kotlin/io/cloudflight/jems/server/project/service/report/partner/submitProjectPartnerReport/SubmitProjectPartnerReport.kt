package io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import io.cloudflight.jems.server.project.service.report.partnerReportSubmitted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZonedDateTime

@Service
class SubmitProjectPartnerReport(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : SubmitProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SubmitProjectPartnerReportException::class)
    override fun submit(partnerId: Long, reportId: Long): ProjectPartnerReportSummary {
        validateReportIsStillDraft(partnerId = partnerId, reportId = reportId)
        val expenditures = validateExpendituresAndSaveCurrencyRates(partnerId = partnerId, reportId = reportId)
        saveExpenditureCostCategory(partnerId = partnerId, reportId, expenditures)

        return reportPersistence.submitReportById(
            partnerId = partnerId,
            reportId = reportId,
            submissionTime = ZonedDateTime.now()
        ).also {
            auditPublisher.publishEvent(
                partnerReportSubmitted(
                    context = this,
                    projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version),
                    report = it,
                )
            )
        }.toSummary()
    }

    private fun validateReportIsStillDraft(partnerId: Long, reportId: Long) {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        if (report.status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateExpendituresAndSaveCurrencyRates(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> {
        val expenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId = reportId)
        val usedCurrencies = expenditures.mapTo(HashSet()) { it.currencyCode }

        val today = LocalDate.now()
        val rates = currencyPersistence.findAllByIdYearAndIdMonth(year = today.year, month = today.monthValue)
            .associateBy { it.code }
            .filterKeys { it in usedCurrencies }

        val notExistingRates = usedCurrencies.minus(rates.keys)
        if (notExistingRates.isNotEmpty())
            throw CurrencyRatesMissing(notExistingRates)

        return reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
            expenditureCosts = expenditures.fillCurrencyRates(rates),
        )
    }

    private fun saveExpenditureCostCategory(partnerId: Long, reportId: Long, expenditures: List<ProjectPartnerReportExpenditureCost>) {
        val options = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId).options
        reportExpenditureCostCategoryPersistence.updateCurrentlyReportedValues(
            partnerId = partnerId,
            reportId = reportId,
            currentlyReported = expenditures.calculateCurrent(options),
        )
    }

    private fun ProjectPartnerReportSubmissionSummary.toSummary() = ProjectPartnerReportSummary(
        id = id,
        reportNumber = reportNumber,
        status = status,
        version = version,
        firstSubmission = firstSubmission,
        createdAt = createdAt,
    )
}
