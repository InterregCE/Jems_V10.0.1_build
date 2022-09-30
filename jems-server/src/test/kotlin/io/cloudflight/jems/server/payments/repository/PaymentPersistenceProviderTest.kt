package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentType
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumId
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.toModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.utils.projectEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class PaymentPersistenceProviderTest: UnitTest() {

    @RelaxedMockK
    lateinit var paymentRepository: PaymentRepository
    @RelaxedMockK
    lateinit var paymentPartnerRepository: PaymentPartnerRepository
    @RelaxedMockK
    lateinit var paymentPartnerInstallmentRepository: PaymentPartnerInstallmentRepository

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository
    @RelaxedMockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository
    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence
    @RelaxedMockK
    lateinit var projectLumpSumRepository: ProjectLumpSumRepository

    @RelaxedMockK
    lateinit var userRepository: UserRepository
    @RelaxedMockK
    lateinit var fundRepository: ProgrammeFundRepository

    @InjectMockKs
    lateinit var paymentPersistenceProvider: PaymentPersistenceProvider

    companion object {
        private val currentTime = ZonedDateTime.now()
        private const val projectId = 1L
        private const val paymentId = 2L
        private const val lumpSumId = 50L
        private const val fundId = 4L
        private const val partnerId = 5L
        private val dummyCall = createTestCallEntity(10)

        private val account = UserEntity(
            id = 1,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )
        private val dummyProject = ProjectEntity(
            id = projectId,
            call = createTestCallEntity(0, name = "Test Call"),
            customIdentifier = "T1000",
            acronym = "Test Project",
            applicant = dummyCall.creator,
            currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
        )

        private val fund = ProgrammeFundEntity(
            id = fundId,
            selected = true,
            type = ProgrammeFundType.OTHER,
        )

        private val paymentEntity = PaymentEntity(
            id = paymentId,
            type = PaymentType.FTLS,
            project = dummyProject,
            amountApprovedPerFund = BigDecimal(100),
            fund = fund,
            orderNr = 1,
            programmeLumpSumId = lumpSumId
        )

        private val partnerPaymentEntity = PaymentPartnerEntity(
            id = 1L,
            payment = paymentEntity,
            partnerId = partnerId,
            amountApprovedPerPartner = BigDecimal.ONE
        )
        private val projectPartnerEntity = ProjectPartnerEntity(
            abbreviation = "Lead",
            legalStatus = ProgrammeLegalStatusEntity(1),
            project = dummyProject,
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = 1
        )

        private val role = UserRoleEntity(1, "role")
        private val savePaymentUser = UserEntity(4L, "savePaymentInfo@User", "name", "surname", role, "", UserStatus.ACTIVE)
        private val paymentConfirmedUser = UserEntity(5L, "paymentConfirmed@User", "name", "surname", role, "", UserStatus.ACTIVE)
        private val installmentEntity = PaymentPartnerInstallmentEntity(
            id = 3L,
            paymentPartner = partnerPaymentEntity,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentTime.toLocalDate(),
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUser = savePaymentUser,
            savePaymentDate = currentTime.toLocalDate(),
            isPaymentConfirmed = true,
            paymentConfirmedUser = paymentConfirmedUser,
            paymentConfirmedDate = currentTime.toLocalDate()
        )
        private val installmentFirst = PaymentPartnerInstallment(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentTime.toLocalDate(),
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUser = OutputUser(4L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentTime.toLocalDate(),
            isPaymentConfirmed = true,
            paymentConfirmedUser = OutputUser(5L, "paymentConfirmed@User", "name", "surname"),
            paymentConfirmedDate = currentTime.toLocalDate()
        )
        private val installmentUpdate = PaymentPartnerInstallmentUpdate(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentTime.toLocalDate(),
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUserId = 4L,
            savePaymentDate = currentTime.toLocalDate(),
            isPaymentConfirmed = true,
            paymentConfirmedUserId = 5L,
            paymentConfirmedDate = currentTime.toLocalDate()
        )
        private val paymentDetail = PaymentDetail(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectCustomIdentifier = dummyProject.customIdentifier,
            fundName = fund.type.name,
            projectAcronym = dummyProject.acronym,
            amountApprovedPerFund = paymentEntity.amountApprovedPerFund!!,
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
                    partnerAbbreviation = "Lead",
                    amountApprovedPerPartner = BigDecimal.ONE,
                    installments = emptyList()
                )
            )
        )

        private val paymentToCreateEntity = PaymentEntity(
            id = 0L,
            type = PaymentType.FTLS,
            project = dummyProject,
            amountApprovedPerFund = BigDecimal(100),
            fund = fund,
            orderNr = 1,
            programmeLumpSumId = lumpSumId
        )
        private val partnerPaymentCreate = PaymentPartnerToCreate(
            partnerId = partnerId,
            amountApprovedPerPartner = BigDecimal.ONE
        )
        private val paymentToCreateMap = mapOf(Pair(
            PaymentGroupingId(1, fundId),
            PaymentToCreate(lumpSumId, listOf(partnerPaymentCreate), BigDecimal(100))
        ))

        private val expectedPayments = PaymentToProject(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectCustomIdentifier = dummyProject.customIdentifier,
            projectAcronym = "Test Project",
            paymentClaimNo = 0,
            fundName = "OTHER",
            amountApprovedPerFund = BigDecimal(100),
            amountPaidPerFund = BigDecimal.ZERO,
            paymentApprovalDate = currentTime,
            paymentClaimSubmissionDate = null,
            totalEligibleAmount = BigDecimal(10),
            lastApprovedVersionBeforeReadyForPayment = "v1.0"
        )
        private val projectLumpSumId = ProjectLumpSumId(projectId = dummyProject.id, orderNr = 1)
        val programmeLumpSum = programmeLumpSum(id = 50)
        private val contribution1 = ProjectPartnerLumpSumEntity(
            id = ProjectPartnerLumpSumId(
                projectLumpSumId,
                partner(sortNumber = 1, id = 1)
            ),
            amount = BigDecimal.TEN,
        )
        val lumpSumEntity = ProjectLumpSumEntity(
            id = projectLumpSumId,
            programmeLumpSum = programmeLumpSum,
            endPeriod = 7,
            lumpSumContributions = setOf(contribution1),
            paymentEnabledDate = currentTime,
            lastApprovedVersionBeforeReadyForPayment = "v1.0"
        )

        private fun programmeLumpSum(id: Long) = ProgrammeLumpSumEntity(
            id = id,
            translatedValues = combineLumpSumTranslatedValues(
                programmeLumpSumId = id,
                name = setOf(InputTranslation(SystemLanguage.EN, "")),
                description = emptySet()
            ),
            cost = BigDecimal.TEN,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Preparation,
            isFastTrack = false
        )

        private fun partner(sortNumber: Int, id: Long) = ProjectPartnerEntity(
            id = id,
            project = dummyProject,
            abbreviation = "",
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = sortNumber,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
        )
    }

    @Test
    fun getAllPaymentToProject() {
        every { paymentRepository.findAll(Pageable.unpaged()) } returns PageImpl(mutableListOf(paymentEntity))
        every { projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(projectId, 1) } returns lumpSumEntity
        every {
            projectPersistence.getProject(projectId, expectedPayments.lastApprovedVersionBeforeReadyForPayment)
        } returns dummyProject.toModel(null, null, mutableSetOf(), mutableSetOf())

        assertThat(paymentPersistenceProvider.getAllPaymentToProject(Pageable.unpaged()).content)
            .containsAll(listOf(expectedPayments))
    }

    @Test
    fun deleteAllByProjectIdAndOrderNrIn() {
        every { paymentRepository.deleteAllByProjectIdAndOrderNr(projectId, setOf(1))} returns listOf(paymentEntity)
        paymentPersistenceProvider.deleteAllByProjectIdAndOrderNrIn(projectId, setOf(1))
    }

    @Test
    fun savePaymentToProjects() {
        every { projectRepository.getById(projectId)} returns projectEntity
        every { fundRepository.getById(any())} returns fund
        val slotPayments = slot<MutableList<PaymentEntity>>()
        every { paymentRepository.saveAll(capture(slotPayments))} returns mutableListOf(paymentToCreateEntity)
        val slotPartners = slot<MutableList<PaymentPartnerEntity>>()
        every { paymentPartnerRepository.saveAll(capture(slotPartners)) } returns emptyList()

        paymentPersistenceProvider.savePaymentToProjects(projectId, paymentToCreateMap)

        with(slotPayments.captured[0]) {
            assertThat(id).isEqualTo(0)
            assertThat(type).isEqualTo(PaymentType.FTLS)
            assertThat(amountApprovedPerFund).isEqualTo(BigDecimal(100))
        }
        with(slotPartners.captured[0]) {
            assertThat(id).isEqualTo(0)
            assertThat(payment).isEqualTo(paymentToCreateEntity)
            assertThat(partnerId).isEqualTo(partnerId)
            assertThat(amountApprovedPerPartner).isEqualTo(BigDecimal.ONE)
        }
    }

    @Test
    fun getPaymentDetails() {
        every { paymentRepository.getById(paymentId) } returns paymentEntity
        every { paymentPartnerRepository.findAllByPaymentId(paymentId) } returns listOf(partnerPaymentEntity)
        every { projectPartnerRepository.getById(partnerId) } returns projectPartnerEntity

        assertThat(paymentPersistenceProvider.getPaymentDetails(paymentId))
            .isEqualTo(paymentDetail)
    }

    @Test
    fun getPaymentPartnerId() {
        every {
            paymentPartnerRepository
                .getIdByPaymentIdAndPartnerId(partnerPaymentEntity.payment.id, partnerPaymentEntity.partnerId)
        } returns partnerPaymentEntity.id

        assertThat(paymentPersistenceProvider.getPaymentPartnerId(paymentId, partnerId))
            .isEqualTo(partnerPaymentEntity.id)
    }

    @Test
    fun updatePaymentPartnerInstallments() {
        val deleteIds = setOf(2L)
        every { paymentPartnerInstallmentRepository.deleteAllByIdInBatch(deleteIds) } returns Unit
        every { paymentPartnerRepository.getById(partnerPaymentEntity.id) } returns partnerPaymentEntity
        every { userRepository.getById(paymentConfirmedUser.id) } returns paymentConfirmedUser
        every { userRepository.getById(savePaymentUser.id) } returns savePaymentUser
        every {
            paymentPartnerInstallmentRepository.saveAll(any<List<PaymentPartnerInstallmentEntity>>())
        } returns listOf(installmentEntity)

        assertThat(paymentPersistenceProvider.updatePaymentPartnerInstallments(
            paymentPartnerId = partnerPaymentEntity.id,
            toDeleteInstallmentIds = deleteIds,
            paymentPartnerInstallments = listOf(installmentUpdate)
        )).containsExactly(installmentFirst)
    }
}
