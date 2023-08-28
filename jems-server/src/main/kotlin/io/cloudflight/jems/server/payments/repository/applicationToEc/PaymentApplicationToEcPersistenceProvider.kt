package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentApplicationToEcPersistenceProvider(
    private val paymentApplicationsToEcRepository: PaymentApplicationsToEcRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val accountingYearRepository: AccountingYearRepository,
    private val fileRepository: JemsSystemFileService,
    private val reportFileRepository: JemsFileMetadataRepository,
) : PaymentApplicationToEcPersistence {

    @Transactional
    override fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)

        return paymentApplicationsToEcRepository.save(
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
    override fun updatePaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentApplicationsToEcUpdate.id!!)

        existingEcPayment.update(programmeFund, accountingYear, paymentApplicationsToEcUpdate)

        return existingEcPayment.toDetailModel()
    }

    @Transactional
    override fun updatePaymentToEcSummaryOtherSection(paymentToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentToEcUpdate.id!!)

        existingEcPayment.updateOther(paymentToEcUpdate)

        return existingEcPayment.toDetailModel()
    }

    private fun PaymentApplicationToEcEntity.update(
        programmeFundEntity: ProgrammeFundEntity,
        accountingYearEntity: AccountingYearEntity,
        newData: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcEntity {
        this.programmeFund = programmeFundEntity
        this.accountingYear = accountingYearEntity
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
        paymentApplicationsToEcRepository.getById(id).toDetailModel()

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<PaymentApplicationToEc> =
        paymentApplicationsToEcRepository.findAll(pageable).toModel()

    @Transactional
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail =
        paymentApplicationsToEcRepository.getById(paymentId).apply {
            this.status = PaymentEcStatus.Finished
        }.toDetailModel()

    @Transactional
    override fun deleteById(id: Long) {
        paymentApplicationsToEcRepository.deleteById(id)
    }

    @Transactional
    override fun deletePaymentToEcAttachment(fileId: Long) {
        fileRepository.delete(
            reportFileRepository.findByTypeAndId(JemsFileType.PaymentToEcAttachment, fileId)
                ?: throw ResourceNotFoundException("file")
        )
    }

}
