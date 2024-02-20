package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.dto.PaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerInstallmentDTO
import io.cloudflight.jems.api.payments.dto.PaymentSearchRequestDTO
import io.cloudflight.jems.api.payments.dto.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.regular.getPaymentDetail.GetPaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.regular.getPayments.GetPaymentsInteractor
import io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments.UpdatePaymentInstallmentsInteractor
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
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class PaymentsControllerTest : UnitTest() {

    companion object {
        val currentTime = ZonedDateTime.now()
        private val currentDate = LocalDate.now()
        private const val ftlsPaymentId = 1L
        private const val regularPaymentId = 11L
        private const val projectReportNumber = 5
        private const val projectId = 2L
        private const val partnerId = 3L
        private const val lumpSumId = 4L
        private const val paymentClaimId = 5L

        private val call = createTestCallEntity(2)
        private val account = UserEntity(
            id = 1,
            email = "admin@admin.dev",
            sendNotificationsToEmail = false,
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )
        private val project = ProjectEntity(
            id = projectId,
            call = call,
            customIdentifier = "T1000",
            acronym = "project",
            applicant = call.creator,
            currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
        )
        private val fund = ProgrammeFundEntity(
            id = 5L,
            selected = true,
            type = ProgrammeFundType.OTHER
        )
        val ftlsPaymentToProject = PaymentToProject(
            id = ftlsPaymentId,
            paymentType = PaymentType.FTLS,
            projectId = project.id,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            paymentClaimId = null,
            paymentClaimNo = 0,
            paymentToEcId = 6L,
            fundId = 5L,
            fundName = fund.type.name,
            fundAmount = BigDecimal.TEN,
            amountPaidPerFund = BigDecimal.ZERO,
            amountAuthorizedPerFund = BigDecimal.ZERO,
            paymentApprovalDate = currentTime,
            paymentClaimSubmissionDate = null,
            totalEligibleAmount = BigDecimal.TEN,
            lastApprovedVersionBeforeReadyForPayment = "v1.0"
        )

        val regularPaymentToProject = PaymentToProject(
            id = regularPaymentId,
            paymentType = PaymentType.REGULAR,
            projectId = project.id,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            paymentClaimId = paymentClaimId,
            paymentClaimNo = projectReportNumber,
            paymentToEcId = 6L,
            fundId = 5L,
            fundName = fund.type.name,
            fundAmount = BigDecimal.TEN,
            amountPaidPerFund = BigDecimal.ZERO,
            amountAuthorizedPerFund = BigDecimal.ZERO,
            paymentApprovalDate = currentTime,
            paymentClaimSubmissionDate = null,
            totalEligibleAmount = BigDecimal.TEN,
            lastApprovedVersionBeforeReadyForPayment = "v1.0"
        )

        private val installmentFirstDTO = PaymentPartnerInstallmentDTO(
            id = 6L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentTime.toLocalDate(),
            comment = "comment",
            savePaymentInfo = true,
            savePaymentInfoUser = OutputUser(3L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentTime.toLocalDate().plusDays(1),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(3L, "paymentConfirmed@User", "name", "surname"),
            paymentConfirmedDate = currentTime.toLocalDate().plusDays(2),
            correction = null,
        )
        private val installmentFirst = PaymentPartnerInstallment(
            id = installmentFirstDTO.id,
            fundId = fund.id,
            lumpSumId = lumpSumId,
            orderNr = 67,
            amountPaid = installmentFirstDTO.amountPaid,
            paymentDate = currentTime.toLocalDate(),
            comment = installmentFirstDTO.comment,
            isSavePaymentInfo = installmentFirstDTO.savePaymentInfo,
            savePaymentInfoUser = installmentFirstDTO.savePaymentInfoUser,
            savePaymentDate = installmentFirstDTO.savePaymentDate,
            isPaymentConfirmed = installmentFirstDTO.paymentConfirmed,
            paymentConfirmedUser = installmentFirstDTO.paymentConfirmedUser,
            paymentConfirmedDate = installmentFirstDTO.paymentConfirmedDate,
            correction = null,
        )

        private val ftlsPaymentDetail = PaymentDetail(
            id = ftlsPaymentId,
            paymentType = PaymentType.FTLS,
            fundName = fund.type.name,
            projectId = projectId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            spf = false,
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
                    amountApprovedPerPartner = BigDecimal.ONE,
                    installments = listOf(installmentFirst),
                    partnerReportId = null,
                    partnerReportNumber = null
                )
            )
        )

        private val ftlsPaymentDetailDTO = PaymentDetailDTO(
            id = ftlsPaymentId,
            paymentType = PaymentTypeDTO.FTLS,
            fundName = fund.type.name,
            projectId = projectId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            spf = false,
            amountApprovedPerFund = BigDecimal.TEN,
            dateOfLastPayment = null,
            partnerPayments = listOf(
                PaymentPartnerDTO(
                    id = 1L,
                    partnerId = partnerId,
                    partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApproved = BigDecimal.ONE,
                    installments = listOf(installmentFirstDTO),
                    partnerReportId = null,
                    partnerReportNumber = null
                )
            )
        )

        private val regularPaymentDetail = PaymentDetail(
            id = regularPaymentId,
            paymentType = PaymentType.REGULAR,
            fundName = fund.type.name,
            projectId = projectId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            spf = true,
            amountApprovedPerFund = BigDecimal.TEN,
            dateOfLastPayment = null,
            partnerPayments = listOf(
                PartnerPayment(
                    id = 8L,
                    projectId = projectId,
                    orderNr = null,
                    programmeLumpSumId = null,
                    programmeFundId = fund.id,
                    partnerId = partnerId,
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApprovedPerPartner = BigDecimal.ONE,
                    installments = listOf(),
                    partnerReportId = 10L,
                    partnerReportNumber = 5
                )
            )
        )

        private val regularPaymentDetailDTO = PaymentDetailDTO(
            id = regularPaymentId,
            paymentType = PaymentTypeDTO.REGULAR,
            fundName = fund.type.name,
            projectId = projectId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            spf = true,
            amountApprovedPerFund = BigDecimal.TEN,
            dateOfLastPayment = null,
            partnerPayments = listOf(
                PaymentPartnerDTO(
                    id = 8L,
                    partnerId = partnerId,
                    partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerAbbreviation = "partner",
                    amountApproved = BigDecimal.ONE,
                    installments = listOf(),
                    partnerReportId = 10L,
                    partnerReportNumber = 5
                )
            )
        )

        private val dummyFilterDto = PaymentSearchRequestDTO(
            paymentId = 855L,
            paymentType = null,
            projectIdentifiers = setOf("472", "INT00473", ""),
            projectAcronym = "acr-filter",
            claimSubmissionDateFrom = currentDate.minusDays(2),
            claimSubmissionDateTo = currentDate.minusDays(2),
            approvalDateFrom = currentDate.minusDays(3),
            approvalDateTo = currentDate.minusDays(3),
            fundIds = setOf(511L, 512L),
            lastPaymentDateFrom = currentDate.minusDays(1),
            lastPaymentDateTo = currentDate.minusDays(1),
        )

        private val dummyFilter = PaymentSearchRequest(
            paymentId = 855L,
            paymentType = null,
            projectIdentifiers = setOf("472", "INT00473", ""),
            projectAcronym = "acr-filter",
            claimSubmissionDateFrom = currentDate.minusDays(2),
            claimSubmissionDateTo = currentDate.minusDays(2),
            approvalDateFrom = currentDate.minusDays(3),
            approvalDateTo = currentDate.minusDays(3),
            fundIds = setOf(511L, 512L),
            lastPaymentDateFrom = currentDate.minusDays(1),
            lastPaymentDateTo = currentDate.minusDays(1),
            ecPaymentIds = emptySet(),
            contractingScoBasis = null,
            finalScoBasis = null,
        )
    }

    @MockK
    lateinit var getPayments: GetPaymentsInteractor

    @MockK
    lateinit var getPaymentDetail: GetPaymentDetailInteractor

    @MockK
    lateinit var updatePaymentInstallments: UpdatePaymentInstallmentsInteractor

    @InjectMockKs
    private lateinit var controller: PaymentsController

    @Test
    fun getPaymentsToProjects() {
        val slotFilter = slot<PaymentSearchRequest>()
        every { getPayments.getPayments(any(), capture(slotFilter)) } returns PageImpl(listOf(
            ftlsPaymentToProject, regularPaymentToProject))

        assertThat(controller.getPaymentsToProjects(Pageable.unpaged(), dummyFilterDto)).containsExactly(
            PaymentToProjectDTO(
                id = ftlsPaymentId,
                paymentType = PaymentTypeDTO.FTLS,
                projectId = ftlsPaymentToProject.projectId,
                projectCustomIdentifier = ftlsPaymentToProject.projectCustomIdentifier,
                projectAcronym = ftlsPaymentToProject.projectAcronym,
                paymentClaimId = null,
                paymentClaimNo = ftlsPaymentToProject.paymentClaimNo,
                paymentToEcId = ftlsPaymentToProject.paymentToEcId,
                paymentClaimSubmissionDate = ftlsPaymentToProject.paymentClaimSubmissionDate,
                paymentApprovalDate = ftlsPaymentToProject.paymentApprovalDate,
                totalEligibleAmount = ftlsPaymentToProject.totalEligibleAmount,
                fundName = ftlsPaymentToProject.fundName,
                fundAmount = ftlsPaymentToProject.fundAmount,
                amountPaidPerFund = ftlsPaymentToProject.amountPaidPerFund,
                amountAuthorizedPerFund = ftlsPaymentToProject.amountAuthorizedPerFund,
                dateOfLastPayment = null,
                lastApprovedVersionBeforeReadyForPayment = ftlsPaymentToProject.lastApprovedVersionBeforeReadyForPayment
            ),
            PaymentToProjectDTO(
                id = regularPaymentId,
                paymentType = PaymentTypeDTO.REGULAR,
                projectId = regularPaymentToProject.projectId,
                projectCustomIdentifier = regularPaymentToProject.projectCustomIdentifier,
                projectAcronym = regularPaymentToProject.projectAcronym,
                paymentClaimId = regularPaymentToProject.paymentClaimId,
                paymentClaimNo = regularPaymentToProject.paymentClaimNo,
                paymentToEcId = regularPaymentToProject.paymentToEcId,
                paymentClaimSubmissionDate = regularPaymentToProject.paymentClaimSubmissionDate,
                paymentApprovalDate = regularPaymentToProject.paymentApprovalDate,
                totalEligibleAmount = regularPaymentToProject.totalEligibleAmount,
                fundName = regularPaymentToProject.fundName,
                fundAmount = regularPaymentToProject.fundAmount,
                amountPaidPerFund = regularPaymentToProject.amountPaidPerFund,
                amountAuthorizedPerFund = regularPaymentToProject.amountAuthorizedPerFund,
                dateOfLastPayment = null,
                lastApprovedVersionBeforeReadyForPayment = regularPaymentToProject.lastApprovedVersionBeforeReadyForPayment
            ),
        )
        assertThat(slotFilter.captured).isEqualTo(dummyFilter)
    }

    @Test
    fun `getPaymentsToProjects - emptyFilter`() {
        val slotFilter = slot<PaymentSearchRequest>()
        every { getPayments.getPayments(any(), capture(slotFilter)) } returns PageImpl(emptyList())

        assertThat(controller.getPaymentsToProjects(Pageable.unpaged(), null)).isEmpty()
        assertThat(slotFilter.captured).isEqualTo(PaymentSearchRequest(
            paymentId = null,
            paymentType = null,
            projectIdentifiers = emptySet(),
            projectAcronym = null,
            claimSubmissionDateFrom = null,
            claimSubmissionDateTo = null,
            approvalDateFrom = null,
            approvalDateTo = null,
            fundIds = emptySet(),
            lastPaymentDateFrom = null,
            lastPaymentDateTo = null,
            ecPaymentIds = emptySet(),
            contractingScoBasis = null,
            finalScoBasis = null,
        ))
    }

    @Test
    fun getFtlsPaymentDetail() {
        every { getPaymentDetail.getPaymentDetail(ftlsPaymentId) } returns ftlsPaymentDetail

        assertThat(controller.getPaymentDetail(ftlsPaymentId)).isEqualTo(
            PaymentDetailDTO(
                id = ftlsPaymentId,
                paymentType = PaymentTypeDTO.FTLS,
                projectId = project.id,
                projectCustomIdentifier = project.customIdentifier,
                fundName = fund.type.name,
                projectAcronym = project.acronym,
                spf = false,
                amountApprovedPerFund = BigDecimal.TEN,
                dateOfLastPayment = null,
                partnerPayments = listOf(
                    PaymentPartnerDTO(
                        id = 1L,
                        partnerId = partnerId,
                        partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
                        partnerNumber = 1,
                        partnerAbbreviation = "partner",
                        amountApproved = BigDecimal.ONE,
                        installments = listOf(installmentFirstDTO),
                        partnerReportId = null,
                        partnerReportNumber = null
                    )
                )
            )
        )
    }

    @Test
    fun getRegularPaymentDetail() {
        every { getPaymentDetail.getPaymentDetail(regularPaymentId) } returns regularPaymentDetail

        assertThat(controller.getPaymentDetail(regularPaymentId)).isEqualTo(
            regularPaymentDetailDTO
        )
    }

    @Test
    fun updatePaymentPartnerInstallments() {
        every {
            updatePaymentInstallments.updatePaymentInstallments(ftlsPaymentId, any())
        } returns ftlsPaymentDetail

        assertThat(
            controller.updatePaymentInstallments(ftlsPaymentId, ftlsPaymentDetailDTO)
        ).isEqualTo(ftlsPaymentDetailDTO)
    }
}
