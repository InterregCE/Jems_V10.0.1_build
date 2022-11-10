package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.service.model.AdvancePayment
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentUpdate
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
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class AdvancePaymentPersistenceProviderTest: UnitTest() {

    @RelaxedMockK
    lateinit var advancePaymentRepository: AdvancePaymentRepository
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

    @InjectMockKs
    lateinit var advancePaymentPersistenceProvider: AdvancePaymentPersistenceProvider

    companion object {
        private val currentDate = ZonedDateTime.now().toLocalDate()
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
        private val paymentAuthorizedUser = UserEntity(4L, "savePaymentInfo@User", "name", "surname", role, "", UserStatus.ACTIVE)
        private val paymentConfirmedUser = UserEntity(userId, "paymentConfirmed@User", "name", "surname", role, "", UserStatus.ACTIVE)
        private val project = ProjectFull(
            id = projectId,
            customIdentifier = "identifier",
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

        private val userEntity = UserEntity(
            id = 4L,
            name = paymentAuthorizedUser.name,
            password = paymentAuthorizedUser.password,
            email = paymentAuthorizedUser.email,
            surname = paymentAuthorizedUser.surname,
            userRole = paymentAuthorizedUser.userRole,
            userStatus = UserStatus.ACTIVE
        )
        private val userEntity2 = UserEntity(
            id = userId,
            name = paymentConfirmedUser.name,
            password = paymentConfirmedUser.password,
            email = paymentConfirmedUser.email,
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
            paymentConfirmedDate = currentDate.minusDays(2)
        )

        private val advancePayment = AdvancePayment(
            id = paymentId,
            projectCustomIdentifier = project.customIdentifier,
            projectAcronym = project.acronym,
            partnerType = ProjectPartnerRoleDTO.PARTNER,
            partnerNumber = partnerDetail.sortNumber,
            partnerAbbreviation = partnerDetail.abbreviation,
            programmeFund = fund,
            paymentAuthorized= true,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate.minusDays(3),
            // amountSettled is not yet included
            amountSettled = BigDecimal.ZERO
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

        fun advancePaymentToPersist(paymentId: Long? = null): AdvancePaymentUpdate
           = AdvancePaymentUpdate(
                id = paymentId,
                projectId = projectId,
                partnerId = partnerId,
                programmeFundId = fund.id,
                amountAdvance = BigDecimal.TEN,
                dateOfPayment = currentDate.minusDays(3),
                comment = "comment",
                paymentAuthorized = true,
                paymentAuthorizedUserId = paymentAuthorizedUser.id,
                paymentAuthorizedDate = currentDate.minusDays(3),
                paymentConfirmed = true,
                paymentConfirmedUserId = paymentConfirmedUser.id,
                paymentConfirmedDate = currentDate.minusDays(2)
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

    @Test
    fun list() {
        every {
            advancePaymentRepository.findAll(Pageable.unpaged())
        } returns PageImpl(mutableListOf(advancePaymentEntity))
        every { projectPersistence.getProject(projectId, version) } returns project
        every {
            partnerPersistence.getById(partnerId, version)
        } returns partnerDetail
        every {
            partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version)
        } returns coFinancingContribution
        every {
            partnerCoFinancingPersistence.getSpfCoFinancingAndContributions(partnerId, version)
        } returns coFinancingContributionSpf

        assertThat(advancePaymentPersistenceProvider.list(Pageable.unpaged()).content)
            .containsAll(listOf(advancePayment))
    }

    @Test
    fun getPaymentDetail() {
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity
        every { projectPersistence.getProject(projectId, version) } returns project
        every {
            partnerPersistence.getById(partnerId, version)
        } returns partnerDetail
        every {
            partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version)
        } returns coFinancingContribution
        every {
            partnerCoFinancingPersistence.getSpfCoFinancingAndContributions(partnerId, version)
        } returns coFinancingContributionSpf

        assertThat(advancePaymentPersistenceProvider.getPaymentDetail(paymentId))
            .isEqualTo(advancePaymentDetail)
    }

    @Test
    fun `create advance payment`() {
        every { projectVersion.getLatestApprovedOrCurrent(projectId) } returns "2.0"
        every { projectPersistence.getProject(projectId, "2.0") } returns project
        every { partnerPersistence.getById(partnerId, "2.0").toSummary()} returns partnerDetail.toSummary()

        every { programmeFundRepository.getById(fund.id) } returns ProgrammeFundEntity(fund.id, true)
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { userRepository.getById(userEntity2.id) } returns userEntity2
        every { advancePaymentRepository.save( any() ) } returns advancePaymentEntity

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(AdvancePaymentUpdate(
            id = null,
            projectId = projectId,
            partnerId = partnerId,
            programmeFundId = fund.id,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate.minusDays(3),
            comment = "comment",
            paymentAuthorized = true,
            paymentAuthorizedUserId = paymentAuthorizedUser.id,
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUserId = paymentConfirmedUser.id,
            paymentConfirmedDate = currentDate.minusDays(2)
        ))).isEqualTo(advancePaymentDetail)
    }

    @Test
    fun `update advance payment`() {
        every { projectVersion.getLatestApprovedOrCurrent(projectId) } returns "2.0"
        every { projectPersistence.getProject(projectId, "2.0") } returns project
        every { partnerPersistence.getById(partnerId, "2.0").toSummary()} returns partnerDetail.toSummary()
        every { programmeFundRepository.getById(fund.id) } returns ProgrammeFundEntity(fund.id, true)
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { userRepository.getById(userEntity2.id) } returns userEntity2
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity

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
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity

        val toBeSavedSlot = slot<AdvancePaymentEntity>()
        every { advancePaymentRepository.save(capture(toBeSavedSlot)) } returns advancePaymentEntity

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(
            advancePaymentToPersist(2L).copy(
                programmeFundId = null,
                partnerContributionId = contribSourceId
            )
        )).isEqualTo(advancePaymentDetail)
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
        every { advancePaymentRepository.getById(paymentId) } returns advancePaymentEntity

        val toBeSavedSlot = slot<AdvancePaymentEntity>()
        every { advancePaymentRepository.save(capture(toBeSavedSlot)) } returns advancePaymentEntity

        assertThat(advancePaymentPersistenceProvider.updatePaymentDetail(
            advancePaymentToPersist(2L).copy(
                programmeFundId = null,
                partnerContributionId = null,
                partnerContributionSpfId = contribSourceId
            )
        )).isEqualTo(advancePaymentDetail)
        assertThat(toBeSavedSlot.captured.programmeFund).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionId).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionName).isNull()
        assertThat(toBeSavedSlot.captured.partnerContributionSpfId).isEqualTo(contribSourceId)
        assertThat(toBeSavedSlot.captured.partnerContributionSpfName).isEqualTo("name")
    }
}
