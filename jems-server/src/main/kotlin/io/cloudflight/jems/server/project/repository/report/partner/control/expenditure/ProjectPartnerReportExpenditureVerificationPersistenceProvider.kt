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
import java.math.BigDecimal
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportExpenditureVerificationPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val reportExpenditureParkedRepository: PartnerReportParkedExpenditureRepository,
) : ProjectPartnerReportExpenditureVerificationPersistence {

    @Transactional(readOnly = true)
    override fun getPartnerControlReportExpenditureVerification(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureVerification> {
        val controlReportExpVerification = reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
                partnerId = partnerId,
                reportId = reportId,
            ).associateBy { it.id }

        return controlReportExpVerification.values.toExtendedModel(
            reportExpenditureParkedRepository.findAllByParkedFromExpenditureIdIn(controlReportExpVerification.keys)
                .associateBy { metadata -> metadata.parkedFromExpenditureId }
        )
    }

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

        return existingEntities.values.toExtendedModel(
            reportExpenditureParkedRepository.findAllByParkedFromExpenditureIdIn(existingEntities.keys)
                .associateBy { metadata -> metadata.parkedFromExpenditureId }
        )
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
            // update rates
            if (newById.containsKey(it.id)) {
                it.currencyConversionRate = newById[it.id]!!.currencyConversionRate
                it.declaredAmountAfterSubmission = newById[it.id]!!.declaredAmountAfterSubmission
            }
            // clear verification
            it.certifiedAmount = it.declaredAmountAfterSubmission ?: BigDecimal.ZERO
            it.deductedAmount = BigDecimal.ZERO
            it.typologyOfErrorId = null
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
