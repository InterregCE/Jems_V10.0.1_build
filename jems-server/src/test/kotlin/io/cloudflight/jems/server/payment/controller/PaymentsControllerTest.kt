package io.cloudflight.jems.server.payment.controller

import io.cloudflight.jems.api.payments.PaymentDetailDTO
import io.cloudflight.jems.api.payments.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.payments.controller.PaymentsController
import io.cloudflight.jems.server.payments.service.getPaymentDetail.GetPaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.getPayments.GetPaymentsInteractor
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class PaymentsControllerTest : UnitTest() {

    companion object {
        private val currentTime = ZonedDateTime.now()
        private const val paymentId = 1L
        private const val projectId = 2L
        private const val partnerId = 3L
        private const val lumpSumId = 4L

        private val call = createTestCallEntity(2)
        private val account = UserEntity(
            id = 1,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )
        private val project = ProjectEntity(
            id = projectId,
            call = call,
            acronym = "project",
            applicant = call.creator,
            currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
        )
        private val fund = ProgrammeFundEntity(
            id = 5L,
            selected = true,
            type = ProgrammeFundType.OTHER
        )
        private val paymentToProject = PaymentToProject(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectId = projectId.toString(),
            projectAcronym = project.acronym,
            paymentClaimNo = 0,
            fundName = fund.type.name,
            amountApprovedPerFund = BigDecimal.TEN,
            amountPaidPerFund = BigDecimal.ZERO,
            paymentApprovalDate = currentTime,
            paymentClaimSubmissionDate = null,
            totalEligibleAmount = BigDecimal.TEN,
            lastApprovedVersionBeforeReadyForPayment = "v1.0"
        )
        private val paymentDetail = PaymentDetail(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectId = projectId,
            fundName = fund.type.name,
            projectAcronym = project.acronym,
            amountApprovedPerFund = BigDecimal.TEN,
            dateOfLastPayment = null,
            partnerPayments = listOf(
                PartnerPayment(
                    id = 1L,
                    projectId = projectId,
                    orderNr = 1,
                    programmeLumpSumId = lumpSumId,
                    programmeFundId = fund.id,
                    partnerId = partnerId,
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApprovedPerPartner = BigDecimal.ONE
                )
            )
        )
    }

    @MockK
    lateinit var getPayments: GetPaymentsInteractor

    @MockK
    lateinit var getPaymentDetail: GetPaymentDetailInteractor

    @InjectMockKs
    private lateinit var controller: PaymentsController

    @Test
    fun getPaymentsToProjects() {
        every { getPayments.getPayments(any()) } returns PageImpl(listOf(paymentToProject))

        assertThat(controller.getPaymentsToProjects(Pageable.unpaged())).containsExactly(
            PaymentToProjectDTO(
                id = paymentId,
                paymentType = PaymentTypeDTO.FTLS,
                projectId = paymentToProject.projectId,
                projectAcronym = paymentToProject.projectAcronym,
                paymentClaimNo = paymentToProject.paymentClaimNo,
                paymentClaimSubmissionDate = paymentToProject.paymentClaimSubmissionDate,
                paymentApprovalDate = paymentToProject.paymentApprovalDate,
                totalEligibleAmount = paymentToProject.totalEligibleAmount,
                fundName = paymentToProject.fundName,
                amountApprovedPerFund = paymentToProject.amountApprovedPerFund,
                amountPaidPerFund = paymentToProject.amountPaidPerFund,
                dateOfLastPayment = null,
                lastApprovedVersionBeforeReadyForPayment = paymentToProject.lastApprovedVersionBeforeReadyForPayment
            )
        )
    }

    @Test
    fun getPaymentDetail() {
        every { getPaymentDetail.getPaymentDetail(paymentId) } returns paymentDetail

        assertThat(controller.getPaymentDetail(paymentId)).isEqualTo(PaymentDetailDTO(
            id = paymentId,
            paymentType = PaymentTypeDTO.FTLS,
            projectId = projectId,
            fundName = fund.type.name,
            projectAcronym = project.acronym,
            amountApprovedPerFund = BigDecimal.TEN,
            dateOfLastPayment = null,
            partnerPayments = listOf(PaymentPartnerDTO(
                id = 1L,
                projectId = projectId,
                partnerId = partnerId,
                partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
                partnerNumber = 1,
                partnerAbbreviation = "partner",
                amountApproved = BigDecimal.ONE
            ))
        ))
    }
}
