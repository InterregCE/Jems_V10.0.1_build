package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentApplicationToEcPersistenceProvider(
    private val ecPaymentRepository: PaymentApplicationsToEcRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val accountingYearRepository: AccountingYearRepository,
    private val fileRepository: JemsSystemFileService,
    private val reportFileRepository: JemsFileMetadataRepository,
) : PaymentApplicationToEcPersistence {

    @Transactional
    override fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)

        return ecPaymentRepository.save(
            PaymentApplicationToEcEntity(
                programmeFund = programmeFund,
                accountingYear = accountingYear,
                status = PaymentEcStatus.Draft,
                nationalReference = paymentApplicationsToEcUpdate.nationalReference,
                technicalAssistanceEur = paymentApplicationsToEcUpdate.technicalAssistanceEur,
                submissionToSfcDate = paymentApplicationsToEcUpdate.submissionToSfcDate,
                sfcNumber = paymentApplicationsToEcUpdate.sfcNumber,
                comment = paymentApplicationsToEcUpdate.comment
            )
        ).toDetailModel()
    }

    @Transactional
    override fun updatePaymentApplicationToEc(
        paymentApplicationId: Long,
        paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcDetail {
        val existingEcPayment = ecPaymentRepository.getById(paymentApplicationId)
        existingEcPayment.update(paymentApplicationsToEcUpdate)
        return existingEcPayment.toDetailModel()
    }

    @Transactional
    override fun updatePaymentToEcSummaryOtherSection(paymentToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val existingEcPayment = ecPaymentRepository.getById(paymentToEcUpdate.id!!)

        existingEcPayment.updateOther(paymentToEcUpdate)

        return existingEcPayment.toDetailModel()
    }

    private fun PaymentApplicationToEcEntity.update(newData: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcEntity {
        this.nationalReference = newData.nationalReference
        this.technicalAssistanceEur = newData.technicalAssistanceEur
        this.submissionToSfcDate = newData.submissionToSfcDate
        this.sfcNumber = newData.sfcNumber
        this.comment = newData.comment
        return this
    }

    private fun PaymentApplicationToEcEntity.updateOther(newData: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcEntity {
        this.nationalReference = newData.nationalReference
        this.technicalAssistanceEur = newData.technicalAssistanceEur
        this.submissionToSfcDate = newData.submissionToSfcDate
        this.sfcNumber = newData.sfcNumber
        this.comment = newData.comment
        return this
    }

    @Transactional(readOnly = true)
    override fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail =
        ecPaymentRepository.getById(id).toDetailModel()

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<PaymentApplicationToEc> =
        ecPaymentRepository.findAll(pageable).toModel()

    @Transactional
    override fun updatePaymentApplicationToEcStatus(
        paymentId: Long,
        status: PaymentEcStatus
    ): PaymentApplicationToEcDetail =
        ecPaymentRepository.getById(paymentId).apply {
            this.status = status
        }.toDetailModel()


    @Transactional
    override fun deleteById(id: Long) {
        ecPaymentRepository.deleteById(id)
    }

    @Transactional
    override fun deletePaymentToEcAttachment(fileId: Long) {
        fileRepository.delete(
            reportFileRepository.findByTypeAndId(JemsFileType.PaymentToEcAttachment, fileId)
                ?: throw ResourceNotFoundException("file")
        )
    }

    @Transactional(readOnly = true)
    override fun existsDraftByFundAndAccountingYear(programmeFundId: Long, accountingYearId: Long): Boolean =
        ecPaymentRepository.existsByProgrammeFundIdAndAccountingYearIdAndStatus(programmeFundId, accountingYearId, PaymentEcStatus.Draft)

    @Transactional(readOnly = true)
    override fun getAvailableAccountingYearsForFund(programmeFundId: Long): List<AccountingYearAvailability> =
        ecPaymentRepository.getAvailableAccountingYearForFund(programmeFundId).map {
            AccountingYearAvailability(
                id = it.first.id,
                year = it.first.year,
                startDate = it.first.startDate,
                endDate = it.first.endDate,
                available = it.second == 0,
            )
        }

}
