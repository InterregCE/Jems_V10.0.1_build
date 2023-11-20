package io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments

import io.cloudflight.jems.api.payments.dto.PaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerDTO
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.paymentInstallmentAuthorized
import io.cloudflight.jems.server.payments.service.paymentInstallmentConfirmed
import io.cloudflight.jems.server.payments.service.paymentInstallmentDeleted
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.payments.service.toModelList
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UpdatePaymentInstallments(
    private val paymentPersistence: PaymentPersistence,
    private val securityService: SecurityService,
    private val validator: PaymentInstallmentsValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
) : UpdatePaymentInstallmentsInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(UpdatePaymentInstallmentsException::class)
    override fun updatePaymentInstallments(
        paymentId: Long,
        paymentDetail: PaymentDetailDTO
    ): PaymentDetail {
        validatePartnerPayments(paymentId, paymentDetail.partnerPayments)
        validateCorrections(paymentDetail)
        for ((index, partnerPayment) in paymentDetail.partnerPayments.withIndex()) {
            val installments = partnerPayment.installments.toModelList()
            val partnerId = partnerPayment.partnerId

            val savedInstallments = paymentPersistence.findPaymentPartnerInstallments(partnerPayment.id)
            // find Installments for deletion
            val deleteInstallments = savedInstallments.filter { saved -> saved.id !in installments.map { it.id } }

            // prevent deletion if isSavePaymentInfo, validate data
            validator.validateInstallments(installments, savedInstallments, deleteInstallments, index)

            // calculate
            // - savePaymentInfoUserId, savePaymentDate by isSavePaymentInfo
            // - paymentConfirmedUserId, paymentConfirmedDate by isPaymentConfirmed
            val currentUserId = securityService.getUserIdOrThrow()
            val today = LocalDate.now()
            installments.forEach { toUpdate ->
                calculateCheckboxValues(
                    currentUserId = currentUserId,
                    currentDate = today,
                    old = savedInstallments.find { toUpdate.id == it.id },
                    update = toUpdate
                )
            }

            // load project, partner for audit
            val payment = paymentPersistence.getPaymentDetails(paymentId)
            val partner = payment.partnerPayments.find { it.partnerId == partnerId }

            paymentPersistence.updatePaymentPartnerInstallments(
                paymentPartnerId = partnerPayment.id,
                toDeleteInstallmentIds = deleteInstallments.mapNotNull { it.id }.toHashSet(),
                paymentPartnerInstallments = installments
            ).also {
                deleteInstallments.forEach { deletedInstallment ->
                    val instNr = savedInstallments.indexOfFirst { it.id == deletedInstallment.id }
                    auditPublisher.publishEvent(paymentInstallmentDeleted(this, payment, partner!!, instNr + 1))
                }
                installments.forEachIndexed { instNr, installment ->
                    val oldInstallment = savedInstallments.find { installment.id == it.id }

                    // authorised
                    if (installment.isSavePaymentInfo == true &&
                        (oldInstallment == null || oldInstallment.isSavePaymentInfo != true)
                    ) {
                        auditPublisher.publishEvent(paymentInstallmentAuthorized(this, payment, partner!!, installment, instNr + 1))
                    }

                    // get all that were confirmed
                    if (installment.isPaymentConfirmed == true &&
                        (oldInstallment == null || oldInstallment.isPaymentConfirmed != true)
                    ) {
                        auditPublisher.publishEvent(paymentInstallmentConfirmed(this, payment, partner!!, installment, instNr + 1))
                    }
                }
            }
        }

        return paymentPersistence.getPaymentDetails(paymentId)
    }

    private fun calculateCheckboxValues(
        currentUserId: Long,
        currentDate: LocalDate,
        old: PaymentPartnerInstallment?,
        update: PaymentPartnerInstallmentUpdate
    ) {
        // savePaymentInfo checkbox was selected
        if (update.isSavePaymentInfo == true) {
            if (old == null || old.isSavePaymentInfo == false) {
                update.savePaymentInfoUserId = currentUserId
                update.savePaymentDate = currentDate
            } else {
                update.savePaymentInfoUserId = old.savePaymentInfoUser?.id
                update.savePaymentDate = old.savePaymentDate
            }
        } // else: unselected - leave null for update
        // paymentConfirmed checkbox was selected
        if (update.isPaymentConfirmed == true) {
            if (old == null || old.isPaymentConfirmed == false) {
                update.paymentConfirmedUserId = currentUserId
                update.paymentConfirmedDate = currentDate
            } else {
                update.paymentConfirmedUserId = old.paymentConfirmedUser?.id
                update.paymentConfirmedDate = old.paymentConfirmedDate
            }
        } // else: unselected - leave null for update
    }

    private fun validatePartnerPayments(paymentId: Long, partnerPayment: List<PaymentPartnerDTO>) {
        paymentPersistence.getPaymentPartnersIdsByPaymentId(paymentId).also { paymentPartnerIds ->
            if (partnerPayment.any { it.id !in paymentPartnerIds }) {
                throw PaymentPartnerNotValidException()
            }
        }
    }

    private fun validateCorrections(paymentDetail: PaymentDetailDTO) {
        val correctionIdsByProjectId = auditControlCorrectionPersistence.getAllIdsByProjectId(projectId = paymentDetail.projectId)
        val paymentInstallmentCorrectionIds = paymentDetail.partnerPayments.flatMap { payment ->
            payment.installments.mapNotNull { installment ->
                installment.correction?.id
            }
        }
        val invalidCorrectionIds = paymentInstallmentCorrectionIds.minus(correctionIdsByProjectId)
        if (invalidCorrectionIds.isNotEmpty()) {
            throw CorrectionsNotValidException(invalidCorrectionIds)
        }
    }
}
