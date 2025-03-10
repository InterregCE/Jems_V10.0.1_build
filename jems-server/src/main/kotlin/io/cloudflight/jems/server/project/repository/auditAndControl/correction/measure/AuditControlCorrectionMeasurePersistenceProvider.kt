package io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure

import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.repository.account.finance.correction.PaymentAccountCorrectionExtensionRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection.EcPaymentCorrectionExtensionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.AuditControlCorrectionMeasurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AuditControlCorrectionMeasurePersistenceProvider(
    private val programmeMeasureRepository: CorrectionProgrammeMeasureRepository,
    private val ecPaymentCorrectionExtensionRepository: EcPaymentCorrectionExtensionRepository,
    private val paymentAccountCorrectionExtensionRepository: PaymentAccountCorrectionExtensionRepository,
) : AuditControlCorrectionMeasurePersistence {

    @Transactional(readOnly = true)
    override fun getProgrammeMeasure(correctionId: Long): ProjectCorrectionProgrammeMeasure =
        programmeMeasureRepository.getByCorrectionId(correctionId = correctionId)
            .toModel(accountingYear = fetchAccountingYear(correctionId))

    @Transactional
    override fun updateProgrammeMeasure(
        correctionId: Long,
        programmeMeasure: ProjectCorrectionProgrammeMeasureUpdate
    ): ProjectCorrectionProgrammeMeasure =
        programmeMeasureRepository.getByCorrectionId(correctionId = correctionId).apply {
            scenario = programmeMeasure.scenario
            comment = programmeMeasure.comment
        }.toModel(accountingYear = fetchAccountingYear(correctionId))


    private fun fetchAccountingYear(correctionId: Long): AccountingYear? {
        val paymentToEcAccountingYear = ecPaymentCorrectionExtensionRepository.getAccountingYearByCorrectionId(correctionId)
        val paymentAccountAccountingYear = paymentAccountCorrectionExtensionRepository.getAccountingYearByCorrectionId(correctionId)
        return (paymentToEcAccountingYear ?: paymentAccountAccountingYear)?.toModel()
    }

}
