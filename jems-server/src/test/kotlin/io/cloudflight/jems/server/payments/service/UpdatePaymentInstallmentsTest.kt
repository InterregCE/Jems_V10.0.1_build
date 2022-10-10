package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.model.PaymentType
import io.cloudflight.jems.server.payments.service.updatePaymentInstallments.PaymentInstallmentsValidator
import io.cloudflight.jems.server.payments.service.updatePaymentInstallments.UpdatePaymentInstallments
import io.cloudflight.jems.server.payments.service.updatePaymentInstallments.UpdatePaymentInstallmentsException
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdatePaymentInstallmentsTest : UnitTest() {

    companion object {
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private const val paymentId = 1L
        private const val partnerId = 2L
        private const val projectId = 3L
        private const val paymentPartnerId = 6L
        private const val currentUserId = 7L

        private val paymentDetail = PaymentDetail(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectCustomIdentifier = "customIdentifier",
            fundName = "name",
            projectAcronym = "acronym",
            amountApprovedPerFund = BigDecimal.TEN,
            partnerPayments = listOf(
                PartnerPayment(
                    id = 1L,
                    projectId = projectId,
                    orderNr = 1,
                    programmeLumpSumId = 4L,
                    programmeFundId = 5L,
                    partnerId = partnerId,
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApprovedPerPartner = BigDecimal.ONE,
                    installments = emptyList()
                )
            )
        )
        private val outputUser = OutputUser(currentUserId, "payment@User", "name", "surname")
        private val installmentUpdate = PaymentPartnerInstallmentUpdate(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "commentModified",
            isSavePaymentInfo = true,
            savePaymentInfoUserId = 4L,
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUserId = null,
            paymentConfirmedDate = null
        )
        private val installment = PaymentPartnerInstallment(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUser = OutputUser(9L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentDate,
            isPaymentConfirmed = false,
            paymentConfirmedUser = null,
            paymentConfirmedDate = null
        )
        private val installmentToDelete = PaymentPartnerInstallment(
            id = 4L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "comment",
            isSavePaymentInfo = false,
            isPaymentConfirmed = false
        )
        private val installmentUpdateNew = PaymentPartnerInstallmentUpdate(
            id = 5L,
            amountPaid = BigDecimal.ONE,
            paymentDate = currentDate.minusDays(1),
            comment = null,
            isSavePaymentInfo = true,
            isPaymentConfirmed = false
        )
        private val installmentNew = PaymentPartnerInstallment(
            id = 5L,
            amountPaid = BigDecimal.ONE,
            paymentDate = currentDate.minusDays(1),
            comment = null,
            isSavePaymentInfo = true,
            savePaymentInfoUser = outputUser,
            savePaymentDate = currentDate,
            isPaymentConfirmed = false
        )
    }

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var validator: PaymentInstallmentsValidator

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updatePaymentInstallments: UpdatePaymentInstallments

    @Test
    fun `update installments for a payment partner`() {
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every { paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId) } returns listOf(installment)
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every { validator.validateInstallmentDeletion(emptyList()) } returns Unit
        every { validator.validateMaxInstallments(listOf(installmentUpdate)) } returns Unit
        every { validator.validateInstallmentValues(listOf(installmentUpdate)) } returns Unit
        every { validator.validateCheckboxStates(listOf(installmentUpdate)) } returns Unit
        every { paymentPersistence.getPaymentDetails(paymentId) } returns paymentDetail

        val toUpdateSlot = slot<List<PaymentPartnerInstallmentUpdate>>()
        every {
            paymentPersistence.updatePaymentPartnerInstallments(paymentPartnerId, emptySet(), capture(toUpdateSlot))
        } returns listOf(installment)

        assertThat(updatePaymentInstallments.updatePaymentPartnerInstallments(
            paymentId = paymentId,
            partnerId = partnerId,
            installments = listOf(installmentUpdate))
        ).containsExactly(installment)
        assertThat(toUpdateSlot.captured).containsExactly(
            PaymentPartnerInstallmentUpdate(
                id = 3L,
                amountPaid = BigDecimal.TEN,
                paymentDate = currentDate,
                comment = installmentUpdate.comment,
                isSavePaymentInfo = true,
                savePaymentInfoUserId = installmentUpdate.savePaymentInfoUserId,
                savePaymentDate = currentDate,
                isPaymentConfirmed = true,
                paymentConfirmedUserId = outputUser.id,
                paymentConfirmedDate = currentDate
            )
        )
    }

    @Test
    fun `update installments for a payment partner - multiple installments`() {
        val updates = listOf(installmentUpdate, installmentUpdateNew)
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every {
            paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId)
        } returns listOf(installment, installmentToDelete)
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every { validator.validateInstallmentDeletion(listOf(installmentToDelete)) } returns Unit
        every { validator.validateMaxInstallments(updates) } returns Unit
        every { validator.validateInstallmentValues(updates) } returns Unit
        every { validator.validateCheckboxStates(updates) } returns Unit
        every { paymentPersistence.getPaymentDetails(paymentId) } returns paymentDetail

        val toUpdateSlot = slot<List<PaymentPartnerInstallmentUpdate>>()
        every {
            paymentPersistence.updatePaymentPartnerInstallments(paymentPartnerId, setOf(4L), capture(toUpdateSlot))
        } returns listOf(installment, installmentNew)
        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers {}

        assertThat(updatePaymentInstallments.updatePaymentPartnerInstallments(
            paymentId = paymentId,
            partnerId = partnerId,
            installments = updates)
        ).containsExactly(installment, installmentNew)
        assertThat(toUpdateSlot.captured).containsExactly(
            PaymentPartnerInstallmentUpdate(
                id = 3L,
                amountPaid = BigDecimal.TEN,
                paymentDate = currentDate,
                comment = installmentUpdate.comment,
                isSavePaymentInfo = true,
                savePaymentInfoUserId = installmentUpdate.savePaymentInfoUserId,
                savePaymentDate = currentDate,
                isPaymentConfirmed = true,
                paymentConfirmedUserId = outputUser.id,
                paymentConfirmedDate = currentDate
            ),
            PaymentPartnerInstallmentUpdate(
                id = installmentNew.id,
                amountPaid = installmentNew.amountPaid,
                paymentDate = installmentNew.paymentDate,
                comment = installmentNew.comment,
                isSavePaymentInfo = true,
                savePaymentInfoUserId = installmentUpdateNew.savePaymentInfoUserId,
                savePaymentDate = currentDate,
                isPaymentConfirmed = false,
                paymentConfirmedUserId = null,
                paymentConfirmedDate = null
            )
        )

        verify(exactly = 3) { auditPublisher.publishEvent(any()) }
        assertThat(slotAudit[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_INSTALLMENT_IS_DELETED,
                project = AuditProject(projectId.toString(), paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym),
                entityRelatedId =null,
                description = "Payment installment 2 for payment 1 of partner LP1 is deleted"
            )
        )
        assertThat(slotAudit[1].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_INSTALLMENT_CONFIRMED,
                project = AuditProject(projectId.toString(), paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym),
                entityRelatedId =null,
                description = "Payment details for payment 1, installment 1 of partner LP1 are confirmed"
            )
        )
        assertThat(slotAudit[2].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_INSTALLMENT_SAVED,
                project = AuditProject(projectId.toString(), paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym),
                entityRelatedId =null,
                description = "Payment details for payment 1, installment 2 of partner LP1 are saved"
            )
        )
    }

    @Test
    fun `update installments for a payment partner - error`() {
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every { paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId) } returns listOf(installment)
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every { validator.validateInstallmentDeletion(emptyList()) } returns Unit
        every { validator.validateMaxInstallments(listOf(installmentUpdate)) } returns Unit
        every { validator.validateInstallmentValues(listOf(installmentUpdate)) } returns Unit
        every { validator.validateCheckboxStates(listOf(installmentUpdate)) } returns Unit
        every { paymentPersistence.getPaymentDetails(paymentId) } returns paymentDetail
        every {
            paymentPersistence.updatePaymentPartnerInstallments(paymentPartnerId, emptySet(), any())
        } throws UpdatePaymentInstallmentsException(Exception())

        val exception = assertThrows<UpdatePaymentInstallmentsException> {
            updatePaymentInstallments.updatePaymentPartnerInstallments(
                paymentId = paymentId,
                partnerId = partnerId,
                installments = listOf(installmentUpdate)
            )
        }
        assertThat(exception.code).isEqualTo("S-UPPI")
    }

    @Test
    fun `update installments for a payment partner - invalid`() {
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every { paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId) } returns listOf(installment)
        every {
            validator.validateInstallmentDeletion(emptyList())
        } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updatePaymentInstallments.updatePaymentPartnerInstallments(
                paymentId = paymentId,
                partnerId = partnerId,
                installments = listOf(installmentUpdate)
            )
        }
    }
}
