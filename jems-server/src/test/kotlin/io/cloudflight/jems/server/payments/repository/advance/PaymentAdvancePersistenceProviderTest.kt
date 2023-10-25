package io.cloudflight.jems.server.payments.repository.advance

import com.querydsl.core.types.dsl.BooleanOperation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.AdvancePaymentSettlementEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.ProjectVersionPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.PartnerPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.repository.toSettingsModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.toOutputUser
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class PaymentAdvancePersistenceProviderTest: UnitTest() {

    @MockK
    private lateinit var advancePaymentRepository: AdvancePaymentRepository
    @RelaxedMockK
    lateinit var projectVersion: ProjectVersionPersistenceProvider
    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var partnerPersistence: PartnerPersistenceProvider
    @RelaxedMockK
    lateinit var partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistenceProvider

    @RelaxedMockK
    lateinit var userRepository: UserRepository
    @RelaxedMockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

    @MockK
    lateinit var reportFileRepository: JemsFileMetadataRepository
    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @InjectMockKs
    private lateinit var advancePaymentPersistenceProvider: PaymentAdvancePersistenceProvider

    companion object {
        private val currentDate = LocalDate.of(2023, 6, 19)
        private const val projectId = 1L
        private const val version = "2.0"
        private const val paymentId = 2L
        private const val partnerId = 5L
        private const val fundId = 4L
        private const val contribSourceId = 5L
        private const val userId = 6L
        private val dummyCall = createTestCallEntity(10)

        private val fundEntity = ProgrammeFundEntity(fundId, true)
        private val fund = ProgrammeFund(fundId, true)
        private val role = UserRoleEntity(1, "role")
        private val paymentAuthorizedUser = UserEntity(4L, "savePaymentInfo@User", false, "name", "surname", role, "", UserStatus.ACTIVE)
        private val paymentConfirmedUser = UserEntity(userId, "paymentConfirmed@User", false, "name", "surname", role, "", UserStatus.ACTIVE)
        private val project = ProjectFull(
            id = projectId,
            customIdentifier = "identifier",
            callSettings = dummyCall.toSettingsModel(mutableSetOf(), mutableSetOf(), false),
            acronym = "acronym",
            applicant = mockk(),
            projectStatus = ProjectStatus(
                status = ApplicationStatus.APPROVED,
                user = paymentAuthorizedUser.toUserSummary(),
                updated = ZonedDateTime.now()
            ),
            duration = 11
        )

        private val userEntity = UserEntity(
            id = 4L,
            name = paymentAuthorizedUser.name,
            password = paymentAuthorizedUser.password,
            email = paymentAuthorizedUser.email,
            sendNotificationsToEmail = false,
            surname = paymentAuthorizedUser.surname,
            userRole = paymentAuthorizedUser.userRole,
            userStatus = UserStatus.ACTIVE
        )
        private val userEntity2 = UserEntity(
            id = userId,
            name = paymentConfirmedUser.name,
            password = paymentConfirmedUser.password,
            email = paymentConfirmedUser.email,
            sendNotificationsToEmail = false,
            surname = paymentConfirmedUser.surname,
            userRole = paymentConfirmedUser.userRole,
            userStatus = UserStatus.ACTIVE
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

        private val advancePaymentEntity = AdvancePaymentEntity(
            id = paymentId,
            projectId = projectId,
            projectVersion = version,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            partnerId = partnerId,
            partnerRole = partnerDetail.role,
            partnerSortNumber = partnerDetail.sortNumber,
            partnerAbbreviation = partnerDetail.abbreviation,
            programmeFund = fundEntity,
            partnerContributionId = null,
            partnerContributionSpfId = null,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate.minusDays(3),
            comment = "comment",
            isPaymentAuthorizedInfo = true,
            paymentAuthorizedInfoUser = paymentAuthorizedUser,
            paymentAuthorizedDate = currentDate.minusDays(3),
            isPaymentConfirmed = true,
            paymentConfirmedUser = paymentConfirmedUser,
            paymentConfirmedDate = currentDate.minusDays(2),
        )

        private fun advancePaymentEntity(settlements: MutableSet<AdvancePaymentSettlementEntity>) =
            advancePaymentEntity.apply { this.paymentSettlements = settlements }

        private val advancePaymentSettlementEntity = AdvancePaymentSettlementEntity(
            id = 1L,
            number = 1,
            advancePayment = advancePaymentEntity,
            amountSettled = BigDecimal(5),
            settlementDate = currentDate.minusDays(1),
            comment = "half"
        )


        private val paymentSettlement = AdvancePaymentSettlement(
            id = 1L,
            number = 1,
            amountSettled = BigDecimal(5),
            settlementDate = currentDate.minusDays(1),
            comment = "half"
        )

        private val advancePayment = AdvancePayment(
            id = paymentId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            partnerType = ProjectPartnerRole.PARTNER,
            partnerSortNumber = partnerDetail.sortNumber,
            partnerAbbreviation = partnerDetail.abbreviation,
            programmeFund = fund,
            paymentAuthorized= true,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate.minusDays(3),
            // amountSettled is not yet included
            amountSettled = BigDecimal.ZERO,
            paymentSettlements = listOf(paymentSettlement)
        )

        private val advancePaymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            projectVersion = "2.0",
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.PARTNER,
            partnerNumber = partnerDetail.sortNumber,
            partnerAbbreviation = partnerDetail.abbreviation,
            programmeFund = fund,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate.minusDays(3),
            comment = "comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = paymentAuthorizedUser.toOutputUser(),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = paymentConfirmedUser.toOutputUser(),
            paymentConfirmedDate = currentDate.minusDays(2),
            paymentSettlements = listOf(paymentSettlement)
        )

        fun advancePaymentToPersist(paymentId: Long? = null): AdvancePaymentUpdate
           = AdvancePaymentUpdate(
                id = paymentId,
                projectId = projectId,
                partnerId = partnerId,
                programmeFundId = fund.id,
                amountPaid = BigDecimal.TEN,
                paymentDate = currentDate.minusDays(3),
                comment = "comment",
                paymentSettlements = listOf(paymentSettlement)
           )

        private val contribution = ProjectPartnerContribution(
            id = contribSourceId,
            name = "contribution",
            status = ProjectPartnerContributionStatusDTO.Public,
            isPartner = true,
            amount = BigDecimal.ONE
        )
        private val coFinancingContribution = ProjectPartnerCoFinancingAndContribution(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    percentage = BigDecimal.valueOf(20.5),
                    fund = fund
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                    percentage = BigDecimal.valueOf(50.5),
                    fund = null
                )
            ),
            partnerContributions = listOf(contribution),
            partnerAbbreviation = "PartnerName"
        )
        private val coFinancingContributionSpf = ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    percentage = BigDecimal.valueOf(20.5),
                    fund = ProgrammeFund(id = 10, selected = true, type = ProgrammeFundType.ERDF)
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContributionSpf(
                    id = contribSourceId,
                    name = "name",
                    status = ProjectPartnerContributionStatusDTO.Public,
                    amount = BigDecimal.TEN
                )
            )
        )
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(advancePaymentRepository)
    }

    @Test
    fun list() {
        val filters = AdvancePaymentSearchRequest(
            paymentId = 855L,
            projectIdentifiers = setOf("472", "INT00473", ""),
            projectAcronym = "acr-filter",
            fundIds = setOf(511L, 512L),
            amountFrom = BigDecimal.ONE,
            amountTo = BigDecimal.TEN,
            dateFrom = currentDate.minusDays(1),
            dateTo = currentDate.plusDays(1),
            authorized = true,
            confirmed = false,
        )
        val slotPredicate = slot<BooleanOperation>()
        every { advancePaymentRepository.findAll(capture(slotPredicate), Pageable.unpaged()) } returns
                PageImpl(mutableListOf(advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity))))

        assertThat(advancePaymentPersistenceProvider.list(Pageable.unpaged(), filters))
            .containsAll(listOf(advancePayment))
        assertThat(slotPredicate.captured.toString()).isEqualTo(
            "advancePaymentEntity.id = 855 && " +
                    "(advancePaymentEntity.projectId = 472 || advancePaymentEntity.projectCustomIdentifier in [472, INT00473]) && " +
                    "containsIc(advancePaymentEntity.projectAcronym,acr-filter) && " +
                    "advancePaymentEntity.programmeFund.id in [511, 512] && " +
                    "advancePaymentEntity.amountPaid >= 1 && " +
                    "advancePaymentEntity.amountPaid <= 10 && " +
                    "advancePaymentEntity.paymentDate >= 2023-06-18 && " +
                    "advancePaymentEntity.paymentDate <= 2023-06-20 && " +
                    "advancePaymentEntity.isPaymentAuthorizedInfo = true && " +
                    "advancePaymentEntity.isPaymentConfirmed = false"
        )
    }

    @Test
    fun existsById() {
        every { advancePaymentRepository.existsById(7L) } returns true
        every { advancePaymentRepository.existsById(-1L) } returns false
        assertThat(advancePaymentPersistenceProvider.existsById(7L)).isTrue()
        assertThat(advancePaymentPersistenceProvider.existsById(-1L)).isFalse()
    }

    @Test
    fun getPaymentsByProjectId() {
        every { advancePaymentRepository.findAllByProjectId(86L) } returns listOf(advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity)))
        assertThat(advancePaymentPersistenceProvider.getPaymentsByProjectId(86L)).containsExactly(advancePayment)
    }

    @Test
    fun deleteByPaymentId() {
        every { advancePaymentRepository.deleteById(25L) } answers { }
        advancePaymentPersistenceProvider.deleteByPaymentId(25L)
        verify(exactly = 1) { advancePaymentRepository.deleteById(25L) }
    }

    @Test
    fun getPaymentDetail() {
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity))
        assertThat(advancePaymentPersistenceProvider.getPaymentDetail(paymentId)).isEqualTo(advancePaymentDetail)
    }

    @Test
    fun `create advance payment`() {
        every { projectVersion.getLatestApprovedOrCurrent(projectId) } returns "2.0"
        every { projectPersistence.getProject(projectId, "2.0") } returns project
        every { partnerPersistence.getById(partnerId, "2.0").toSummary()} returns partnerDetail.toSummary()

        every { programmeFundRepository.getById(fund.id) } returns ProgrammeFundEntity(fund.id, true)
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { userRepository.getById(userEntity2.id) } returns userEntity2
        every { advancePaymentRepository.save(any()) } returnsArgument 0

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(
            AdvancePaymentUpdate(
                id = null,
                projectId = projectId,
                partnerId = partnerId,
                programmeFundId = fund.id,
                amountPaid = BigDecimal.TEN,
                paymentDate = currentDate.minusDays(3),
                comment = "comment",
                paymentSettlements = listOf(paymentSettlement)
            )
        )).isEqualTo(advancePaymentDetail.copy(
            id = 0,
            paymentAuthorized = null,
            paymentAuthorizedDate = null,
            paymentAuthorizedUser = null,
            paymentConfirmed = null,
            paymentConfirmedDate = null,
            paymentConfirmedUser = null
        ))
    }

    @Test
    fun `update advance payment`() {
        every { projectVersion.getLatestApprovedOrCurrent(projectId) } returns "2.0"
        every { projectPersistence.getProject(projectId, "2.0") } returns project
        every { partnerPersistence.getById(partnerId, "2.0").toSummary()} returns partnerDetail.toSummary()
        every { programmeFundRepository.getById(fund.id) } returns ProgrammeFundEntity(fund.id, true)
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { userRepository.getById(userEntity2.id) } returns userEntity2
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity))
        every { advancePaymentRepository.save(any()) } returnsArgument 0

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(advancePaymentToPersist(2L)))
            .isEqualTo(advancePaymentDetail)
    }

    @Test
    fun `update advance payment with source of contribution`() {
        every { projectVersion.getLatestApprovedOrCurrent(projectId) } returns "2.0"
        every { projectPersistence.getProject(projectId, "2.0") } returns project
        every { partnerPersistence.getById(partnerId, "2.0").toSummary() } returns partnerDetail.toSummary()
        every {
            partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, "2.0")
        } returns coFinancingContribution
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { userRepository.getById(userEntity2.id) } returns userEntity2
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity))

        val toBeSavedSlot = slot<AdvancePaymentEntity>()
        every { advancePaymentRepository.save(capture(toBeSavedSlot)) } returnsArgument 0

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(
            advancePaymentToPersist(2L).copy(
                programmeFundId = null,
                partnerContributionId = contribSourceId
            )
        )).isEqualTo(advancePaymentDetail.copy(programmeFund = null, partnerContribution = IdNamePair(5L, "contribution")))
        assertThat(toBeSavedSlot.captured.programmeFund).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionId).isEqualTo(contribSourceId)
        assertThat(toBeSavedSlot.captured.partnerContributionName).isEqualTo("contribution")
    }

    @Test
    fun `update advance payment with source of contribution SPF`() {
        every { projectVersion.getLatestApprovedOrCurrent(projectId) } returns "2.0"
        every { projectPersistence.getProject(projectId, "2.0") } returns project
        every { partnerPersistence.getById(partnerId, "2.0").toSummary() } returns partnerDetail.toSummary()
        every { partnerCoFinancingPersistence.getSpfCoFinancingAndContributions(partnerId, "2.0") } returns coFinancingContributionSpf
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { userRepository.getById(userEntity2.id) } returns userEntity2
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity))

        val toBeSavedSlot = slot<AdvancePaymentEntity>()
        every { advancePaymentRepository.save(capture(toBeSavedSlot)) } returnsArgument 0

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(
            advancePaymentToPersist(2L).copy(
                programmeFundId = null,
                partnerContributionId = null,
                partnerContributionSpfId = contribSourceId
            )
        )).isEqualTo(advancePaymentDetail.copy(programmeFund = null, partnerContributionSpf = IdNamePair(5L, "name")))
        assertThat(toBeSavedSlot.captured.programmeFund).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionId).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionName).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionSpfId).isEqualTo(contribSourceId)
        assertThat(toBeSavedSlot.captured.partnerContributionSpfName).isEqualTo("name")
    }

    @Test
    fun deletePaymentAdvanceAttachment() {
        val file = mockk<JemsFileMetadataEntity>()
        every { fileRepository.delete(file) } answers { }
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentAdvanceAttachment, 16L) } returns file
        advancePaymentPersistenceProvider.deletePaymentAdvanceAttachment(16L)
        verify(exactly = 1) { fileRepository.delete(file) }
    }

    @Test
    fun `deletePaymentAdvanceAttachment - not existing`() {
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentAdvanceAttachment, -1L) } returns null
        assertThrows<ResourceNotFoundException> { advancePaymentPersistenceProvider.deletePaymentAdvanceAttachment(-1L) }
        verify(exactly = 0) { fileRepository.delete(any()) }
    }

    @Test
    fun listForProject() {
        every {
            advancePaymentRepository.findAllByProjectIdAndIsPaymentConfirmedTrue(86L, Pageable.unpaged())
        } returns  PageImpl(listOf(advancePaymentEntity(mutableSetOf(advancePaymentSettlementEntity))))
        assertThat(advancePaymentPersistenceProvider.getConfirmedPaymentsForProject(86L, Pageable.unpaged()).content).containsExactly(advancePayment)
    }
}
