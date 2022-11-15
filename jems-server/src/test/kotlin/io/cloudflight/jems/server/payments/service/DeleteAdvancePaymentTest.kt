package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advance.deleteAdvancePayment.DeleteAdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.repository.toSettingsModel
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.toOutputUser
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class DeleteAdvancePaymentTest: UnitTest() {

    companion object {
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private const val paymentId = 1L
        private const val projectId = 2L
        private const val partnerId = 3L
        private const val fundId = 4L

        private val fund = ProgrammeFund(
            id = fundId,
            selected = true,
            abbreviation = setOf(InputTranslation(SystemLanguage.EN, "FUND")),
        )
        private val role = UserRoleEntity(1, "role")
        private val paymentAuthorizedUser = UserEntity(9L, "savePaymentInfo@User", "name", "surname", role, "", UserStatus.ACTIVE)
        private val paymentConfirmedUser = UserEntity(8L, "paymentConfirmed@User", "name", "surname", role, "", UserStatus.ACTIVE)

        private val dummyCall = createTestCallEntity(10)
        private val project = ProjectFull(
            customIdentifier = "identifier",
            id = projectId,
            callSettings = dummyCall.toSettingsModel(mutableSetOf(), mutableSetOf()),
            acronym = "acronym",
            applicant = mockk(),
            projectStatus = ProjectStatus(
                status = ApplicationStatus.APPROVED,
                user = paymentAuthorizedUser.toUserSummary(),
                updated = ZonedDateTime.now()
            ),
            duration = 11
        )

        private val partnerDetail = ProjectPartnerDetail(
            projectId = projectId,
            id = partnerId,
            active = true,
            abbreviation = "partner",
            role = ProjectPartnerRole.PARTNER,
            nameInOriginalLanguage = "test",
            nameInEnglish = "test",
            createdAt = ZonedDateTime.now(),
            sortNumber = 2,
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = 3L
        )

        private val advancePaymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.PARTNER,
            partnerNumber = partnerDetail.sortNumber,
            partnerAbbreviation = partnerDetail.abbreviation,
            programmeFund = fund,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate.minusDays(3),
            comment = "comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = paymentAuthorizedUser.toOutputUser(),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = paymentConfirmedUser.toOutputUser(),
            paymentConfirmedDate = currentDate.minusDays(2)
        )
    }

    @MockK
    lateinit var  advancePaymentPersistence: PaymentAdvancePersistence

    @RelaxedMockK
    lateinit var  validator: AdvancePaymentValidator

    @MockK
    lateinit var  auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var deleteAdvancePayment: DeleteAdvancePayment

    @Test
    fun `deleted advance payment - OK`() {
        every { advancePaymentPersistence.existsById(paymentId) } returns true
        every { advancePaymentPersistence.getPaymentDetail(paymentId) } returns advancePaymentDetail
        every { validator.validateDeletion(advancePaymentDetail) } just Runs
        every { advancePaymentPersistence.deleteByPaymentId(paymentId) } just Runs
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        deleteAdvancePayment.delete(paymentId)
        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.ADVANCE_PAYMENT_IS_DELETED)
        assertThat(auditSlot.captured.auditCandidate.description)
            .isEqualTo("Advance payment number ${paymentId} is deleted for partner PP2 for funding source FUND")
    }

    @Test
    fun `deleted advance payment - validation failed`() {
        every { advancePaymentPersistence.existsById(paymentId) } returns true
        every { advancePaymentPersistence.getPaymentDetail(paymentId) } returns advancePaymentDetail
        every {
            validator.validateDeletion(advancePaymentDetail)
        } throws I18nValidationException(i18nKey = AdvancePaymentValidator.PAYMENT_ADVANCE_DELETION_ERROR_KEY)

        assertThrows<I18nValidationException> { deleteAdvancePayment.delete(paymentId) }
    }
}
