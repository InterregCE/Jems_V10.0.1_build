package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.toModel
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCurrencyRateChange
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.VerificationAction
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.VerificationAction.ClearDeductions
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.VerificationAction.UpdateCertified
import java.math.BigDecimal
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportExpenditureVerificationPersistenceProvider(
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

    @Transactional(readOnly = true)
    override fun getParkedExpenditureIds(partnerId: Long, reportId: Long): List<Long> {
        val controlReportExpenditureVerificationIds = reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            partnerId = partnerId,
            reportId = reportId,
        ).mapTo(HashSet()) { it.id }

        return reportExpenditureParkedRepository.findAllByParkedFromExpenditureIdIn(controlReportExpenditureVerificationIds)
            .map { it.parkedFromExpenditureId }
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
    override fun updateCurrencyRatesAndPrepareVerification(
        reportId: Long,
        newRates: Collection<ProjectPartnerReportExpenditureCurrencyRateChange>,
        whatToDoWithVerification: VerificationAction,
    ): List<ProjectPartnerReportExpenditureCost> {
        val newById = newRates.associateBy { it.id }

        return reportExpenditureRepository.findByPartnerReportIdOrderByIdDesc(reportId).onEach {
            // update rates
            if (newById.containsKey(it.id)) {
                it.currencyConversionRate = newById[it.id]!!.currencyConversionRate
                it.declaredAmountAfterSubmission = newById[it.id]!!.declaredAmountAfterSubmission
            }
            // update/clear verification
            when (whatToDoWithVerification) {
                ClearDeductions -> it.clearDeductions()
                UpdateCertified -> it.updateCertified()
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

    private fun PartnerReportExpenditureCostEntity.clearDeductions() {
        certifiedAmount = declaredAmountAfterSubmission ?: BigDecimal.ZERO
        deductedAmount = BigDecimal.ZERO
        typologyOfErrorId = null
    }

    private fun PartnerReportExpenditureCostEntity.updateCertified() {
        certifiedAmount = if (parked) BigDecimal.ZERO else (declaredAmountAfterSubmission ?: BigDecimal.ZERO).minus(deductedAmount)
    }

}
