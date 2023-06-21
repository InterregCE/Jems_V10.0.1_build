package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.payments.dto.PaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerInstallmentDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments.PaymentInstallmentsValidator
import io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments.UpdatePaymentInstallments
import io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments.UpdatePaymentInstallmentsException
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

        private val outputUser = OutputUser(currentUserId, "payment@User", "name", "surname")
        private val installmentUpdate = PaymentPartnerInstallmentUpdate(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "commentModified",
            isSavePaymentInfo = true,
            savePaymentInfoUserId = 9L,
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUserId = 7L,
            paymentConfirmedDate = currentDate
        )
        private val installmentNew = PaymentPartnerInstallment(
            id = 5L,
            fundId = 33L,
            lumpSumId = 330L,
            orderNr = 3,
            amountPaid = BigDecimal.ONE,
            paymentDate = currentDate.minusDays(1),
            comment = null,
            isSavePaymentInfo = true,
            savePaymentInfoUser = outputUser,
            savePaymentDate = currentDate,
            isPaymentConfirmed = false
        )
        private val installmentToUpdateTo = PaymentPartnerInstallment(
            id = 3L,
            fundId = 19L,
            lumpSumId = 190L,
            orderNr = 9,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "commentModified",
            isSavePaymentInfo = true,
            savePaymentInfoUser = OutputUser(9L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUser = OutputUser(7L, "savePaymentInfo@User", "name", "surname"),
            paymentConfirmedDate = currentDate
        )

        private val paymentDetail = PaymentDetail(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            fundName = "name",
            projectId = projectId,
            projectCustomIdentifier = "customIdentifier",
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
                    installments = listOf(installmentToUpdateTo)
                )
            )
        )
        private val paymentDetailMultipleInstallments = PaymentDetail(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            fundName = "name",
            projectId = projectId,
            projectCustomIdentifier = "customIdentifier",
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
                    installments = listOf(installmentToUpdateTo, installmentNew)
                )
            )
        )
        private val installment = PaymentPartnerInstallment(
            id = 3L,
            fundId = 19L,
            lumpSumId = 190L,
            orderNr = 9,
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
            fundId = 24L,
            lumpSumId = 240L,
            orderNr = 4,
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
            savePaymentInfoUserId = 7L,
            isPaymentConfirmed = false
        )
        private val installmentDTO = PaymentPartnerInstallmentDTO(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "commentModified",
            savePaymentInfo = true,
            savePaymentInfoUser = OutputUser(9L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentDate,
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(7L, "savePaymentInfo@User", "name", "surname"),
            paymentConfirmedDate = currentDate
        )
        private val installmentNewDTO = PaymentPartnerInstallmentDTO(
            id = 5L,
            amountPaid = BigDecimal.ONE,
            paymentDate = currentDate.minusDays(1),
            comment = null,
            savePaymentInfo = true,
            savePaymentInfoUser = outputUser,
            savePaymentDate = currentDate,
            paymentConfirmed = false,
            paymentConfirmedUser = null,
            paymentConfirmedDate = null
        )
        private val installmentUpdated = PaymentPartnerInstallment(
            id = 3L,
            fundId = 19L,
            lumpSumId = 190L,
            orderNr = 9,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "commentModified",
            isSavePaymentInfo = true,
            savePaymentInfoUser = OutputUser(9L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUser = OutputUser(7L, "savePaymentInfo@User", "name", "surname"),
            paymentConfirmedDate = currentDate
        )
        private val paymentDetailDTO = PaymentDetailDTO(
            id = paymentId,
            paymentType = PaymentTypeDTO.FTLS,
            fundName = "name",
            projectId = projectId,
            projectCustomIdentifier = "customIdentifier",
            projectAcronym = "acronym",
            amountApprovedPerFund = BigDecimal.TEN,
            partnerPayments = listOf(
                PaymentPartnerDTO(
                    id = 1L,
                    partnerId = partnerId,
                    partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApproved = BigDecimal.ONE,
                    installments = listOf(installmentDTO)
                )
            )
        )
        private val paymentDetailMultipleInstallmentsDTO = PaymentDetailDTO(
            id = paymentId,
            paymentType = PaymentTypeDTO.FTLS,
            fundName = "name",
            projectId = projectId,
            projectCustomIdentifier = "customIdentifier",
            projectAcronym = "acronym",
            amountApprovedPerFund = BigDecimal.TEN,
            partnerPayments = listOf(
                PaymentPartnerDTO(
                    id = 1L,
                    partnerId = partnerId,
                    partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApproved = BigDecimal.ONE,
                    installments = listOf(installmentDTO, installmentNewDTO)
                )
            )
        )
    }

    @MockK
    lateinit var paymentPersistence: PaymentRegularPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var validator: PaymentInstallmentsValidator

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updatePaymentInstallments: UpdatePaymentInstallments

    @BeforeEach
    fun reset() {
        clearMocks(paymentPersistence)
    }

    @Test
    fun `update installments for a payment partner`() {
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every { paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId) } returns listOf(installment)
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every { validator.validateInstallments(any(), any(), any(), any()) } returns Unit
        every { paymentPersistence.getPaymentDetails(paymentId) } returns paymentDetail

        val toUpdateSlot = slot<List<PaymentPartnerInstallmentUpdate>>()
        every {
            paymentPersistence.updatePaymentPartnerInstallments(paymentPartnerId, emptySet(), capture(toUpdateSlot))
        } returns listOf(installmentUpdated)

        assertThat(updatePaymentInstallments.updatePaymentInstallments(
            paymentId = paymentId,
            paymentDetail = paymentDetailDTO)
        ).isEqualTo(paymentDetail)
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
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every {
            paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId)
        } returns listOf(installment, installmentToDelete)
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every {
            validator.validateInstallments(any(), any(), any(), any())
        } returns Unit
        every { paymentPersistence.getPaymentDetails(paymentId) } returns paymentDetailMultipleInstallments

        val toUpdateSlot = slot<List<PaymentPartnerInstallmentUpdate>>()
        every {
            paymentPersistence.updatePaymentPartnerInstallments(paymentPartnerId, setOf(4L), capture(toUpdateSlot))
        } returns listOf(installment, installmentNew)
        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers {}

        assertThat(updatePaymentInstallments.updatePaymentInstallments(
            paymentId = paymentId,
            paymentDetail = paymentDetailMultipleInstallmentsDTO)
        ).isEqualTo(paymentDetailMultipleInstallments)
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
                action = AuditAction.PAYMENT_INSTALLMENT_AUTHORISED,
                project = AuditProject(projectId.toString(), paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym),
                entityRelatedId =null,
                description = "Payment details for payment 1, installment 2 of partner LP1 are authorised"
            )
        )
    }

    @Test
    fun `update installments for a payment partner - error`() {
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every { paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId) } returns listOf(installment)
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every { validator.validateInstallments(any(), any(), any(), any()) } returns Unit
        every { paymentPersistence.getPaymentDetails(paymentId) } returns paymentDetail
        every {
            paymentPersistence.updatePaymentPartnerInstallments(paymentPartnerId, emptySet(), any())
        } throws UpdatePaymentInstallmentsException(Exception())

        val exception = assertThrows<UpdatePaymentInstallmentsException> {
            updatePaymentInstallments.updatePaymentInstallments(
                paymentId = paymentId,
                paymentDetail = paymentDetailDTO
            )
        }
        assertThat(exception.code).isEqualTo("S-UPPI")
    }

    @Test
    fun `update installments for a payment partner - invalid`() {
        every { paymentPersistence.getPaymentPartnerId(paymentId, partnerId) } returns paymentPartnerId
        every { paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId) } returns listOf(installment)
        every {
            validator.validateInstallments(any(), any(), any(), any())
        } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updatePaymentInstallments.updatePaymentInstallments(
                paymentId = paymentId,
                paymentDetail = paymentDetailDTO
            )
        }
    }
}
