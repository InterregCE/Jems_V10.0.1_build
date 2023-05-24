package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.toModel
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCurrencyRateChange
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerReportExpenditureVerificationPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
) : ProjectPartnerReportExpenditureVerificationPersistence {

    @Transactional(readOnly = true)
    override fun getPartnerControlReportExpenditureVerification(partnerId: Long, reportId: Long) =
        reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            partnerId = partnerId,
            reportId = reportId,
        ).toExtendedModel()

    @Transactional
    override fun updatePartnerControlReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ExpenditureVerificationUpdate>,
    ): List<ProjectPartnerReportExpenditureVerification> {
        val existingEntities = reportExpenditureRepository
            .findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(reportId, partnerId)
            .associateBy { it.id }

        expenditureVerification.forEach {
            existingEntities.getValue(it.id).updateWith(it)
        }

        return existingEntities.values.toExtendedModel()
    }

    @Transactional
    override fun updateExpenditureCurrencyRatesAndClearVerification(
        partnerId: Long,
        reportId: Long,
        newRates: Collection<ProjectPartnerReportExpenditureCurrencyRateChange>,
    ): List<ProjectPartnerReportExpenditureCost> {
        val reportEntity = reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = reportId)
        val newById = newRates.associateBy { it.id }

        return reportExpenditureRepository.findByPartnerReportOrderByIdDesc(reportEntity).onEach {
            if (newById.containsKey(it.id)) {
                it.currencyConversionRate = newById[it.id]!!.currencyConversionRate
                it.declaredAmountAfterSubmission = newById[it.id]!!.declaredAmountAfterSubmission

                it.partOfSample = false
                it.partOfSampleLocked = false
                it.certifiedAmount = it.declaredAmountAfterSubmission ?: BigDecimal.ZERO
                it.deductedAmount = BigDecimal.ZERO
                it.typologyOfErrorId = null
            }
        }.toModel()
    }

    private fun PartnerReportExpenditureCostEntity.updateWith(newData: ExpenditureVerificationUpdate) {
        certifiedAmount = newData.certifiedAmount
        deductedAmount = newData.deductedAmount
        partOfSample = newData.partOfSample
        typologyOfErrorId = newData.typologyOfErrorId
        parked = newData.parked
        verificationComment = newData.verificationComment
    }

}
