package io.cloudflight.jems.server.payments.repository.regular

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentContributionMetaEntity
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.entity.QAccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.QPaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PartnerPaymentSimple
import io.cloudflight.jems.server.payments.model.regular.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.PartnerContributionSplit
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentFtlsToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentRegularToCreate
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcExtensionRepository
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.entity.fund.QProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.fund.toEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumId
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFtlsTmp
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeAmounts
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeData
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.cloudflight.jems.server.utils.IPA_III_FUND
import io.cloudflight.jems.server.utils.partner.CREATED_AT
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil
import io.cloudflight.jems.server.utils.partner.legalStatusEntity
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
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class PaymentPersistenceProviderTest : UnitTest() {

    @RelaxedMockK
    lateinit var paymentRepository: PaymentRepository

    @RelaxedMockK
    lateinit var paymentPartnerRepository: PaymentPartnerRepository

    @RelaxedMockK
    lateinit var paymentPartnerInstallmentRepository: PaymentPartnerInstallmentRepository

    @MockK
    lateinit var paymentContributionMetaRepository: PaymentContributionMetaRepository

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @RelaxedMockK
    lateinit var projectLumpSumRepository: ProjectLumpSumRepository

    @RelaxedMockK
    lateinit var userRepository: UserRepository

    @RelaxedMockK
    lateinit var fundRepository: ProgrammeFundRepository

    @MockK
    lateinit var reportFileRepository: JemsFileMetadataRepository

    @MockK
    private lateinit var projectReportCoFinancingRepository: ProjectReportCoFinancingRepository

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    private lateinit var fileRepository: JemsProjectFileService

    @MockK
    private lateinit var projectPartnerReportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var paymentToEcExtensionRepository: PaymentToEcExtensionRepository

    @MockK
    private lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository

    @MockK
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    lateinit var paymentPersistenceProvider: PaymentPersistenceProvider

    companion object {
        private val currentDate = LocalDate.of(2023, 7, 11)
        private val currentTime = ZonedDateTime.of(currentDate, LocalTime.of(17, 30, 0, 0), ZoneId.of("Europe/Vienna"))
        private val yesterday = currentTime.minusDays(1)
        private val weekBefore = currentTime.minusWeeks(1)
        private val yearAgo = currentTime.minusYears(1)
        private const val projectId = 1L
        private const val projectReportId = 21L
        private const val paymentId = 2L
        private const val lumpSumId = 50L
        private const val fundId = 4L
        private const val partnerId_5 = 5L
        private const val partnerId_6 = 6L
        private val dummyCall = createTestCallEntity(10)

        fun projectPartnerEntity(
            id: Long,
            role: ProjectPartnerRole = ProjectPartnerRole.LEAD_PARTNER,
            abbreviation: String = "partner",
            sortNumber: Int = 0
        ) = ProjectPartnerEntity(
            id = id,
            project = ProjectPartnerTestUtil.project,
            abbreviation = abbreviation,
            role = role,
            nameInOriginalLanguage = "test",
            createdAt = CREATED_AT,
            nameInEnglish = "test",
            translatedValues = mutableSetOf(),
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = "12",
            pic = "009",
            legalStatus = legalStatusEntity,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            addresses = setOf(
                ProjectPartnerAddressEntity(
                    ProjectPartnerAddressId(
                        id,
                        ProjectPartnerAddressType.Organization
                    ), AddressEntity(country = "AT")
                )
            ),
            sortNumber = sortNumber
        )

        private fun projectReportEntity(id: Long, projectIdentifier: String, projectAcronym: String): ProjectReportEntity {
            val projectReportMock = mockk<ProjectReportEntity>()
            every { projectReportMock.id } returns id
            every { projectReportMock.projectIdentifier } returns projectIdentifier
            every { projectReportMock.projectAcronym } returns projectAcronym
            every { projectReportMock.projectId } returns projectId
            return projectReportMock
        }

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
        private val dummyProject = ProjectEntity(
            id = projectId,
            call = createTestCallEntity(0, name = "Test Call"),
            customIdentifier = "T1000",
            acronym = "Test Project",
            applicant = dummyCall.creator,
            currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
            contractedDecision = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account, updated = weekBefore),
        )

        private val fund = ProgrammeFundEntity(
            id = fundId,
            selected = true,
            type = ProgrammeFundType.OTHER,
            translatedValues = mutableSetOf(
                ProgrammeFundTranslationEntity(TranslationId(mockk(), SystemLanguage.ES), "fund ES abbr", "fund ES desc"),
            ),
        )

        private fun paymentFtlsEntity() = PaymentEntity(
            id = paymentId,
            type = PaymentType.FTLS,
            project = dummyProject,
            projectCustomIdentifier = "proj-cust",
            projectAcronym = "proj-acr",
            projectLumpSum = ProjectLumpSumEntity(
                ProjectLumpSumId(projectId, 13),
                ProgrammeLumpSumEntity(id = lumpSumId, mutableSetOf(), BigDecimal.TEN, false, true, ProgrammeLumpSumPhase.Implementation),
                paymentEnabledDate = currentTime,
                lastApprovedVersionBeforeReadyForPayment = "V4.7",
            ),
            fund = fund,
            amountApprovedPerFund = BigDecimal(100),
            projectReport = null,
        )

        private fun paymentRegularEntity(): PaymentEntity {
            val report = mockk<ProjectReportEntity>()
            every { report.id } returns 777L
            every { report.number } returns 23
            every { report.firstSubmission } returns yesterday
            every { report.verificationEndDate } returns yearAgo
            return PaymentEntity(
                id = paymentId,
                type = PaymentType.REGULAR,
                project = dummyProject,
                projectCustomIdentifier = "proj-cust",
                projectAcronym = "proj-acr",
                projectLumpSum = null,
                fund = fund,
                amountApprovedPerFund = BigDecimal(100),
                projectReport = report,
            )
        }

        private fun partnerPaymentEntity() = PaymentPartnerEntity(
            id = 1L,
            payment = paymentFtlsEntity(),
            projectPartner = projectPartnerEntity,
            partnerCertificate = null,
            partnerAbbreviation = "abbr 1",
            partnerNameInOriginalLanguage = "original 1",
            partnerNameInEnglish = "english 1",
            amountApprovedPerPartner = BigDecimal.ONE,
        )

        private val projectPartnerEntity = ProjectPartnerEntity(
            id = partnerId_5,
            abbreviation = "Lead",
            legalStatus = ProgrammeLegalStatusEntity(1),
            project = dummyProject,
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = 1
        )

        private val role = UserRoleEntity(1, "role")
        private val savePaymentUser = UserEntity(4L, "savePaymentInfo@User", false, "name", "surname", role, "", UserStatus.ACTIVE)
        private val paymentConfirmedUser = UserEntity(5L, "paymentConfirmed@User", false, "name", "surname", role, "", UserStatus.ACTIVE)
        private fun installmentEntity() = PaymentPartnerInstallmentEntity(
            id = 3L,
            paymentPartner = partnerPaymentEntity(),
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate.minusDays(3),
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUser = savePaymentUser,
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUser = paymentConfirmedUser,
            paymentConfirmedDate = currentDate,
            correction = null,
        )

        private val installmentFirst = PaymentPartnerInstallment(
            id = 3L,
            fundId = fundId,
            lumpSumId = lumpSumId,
            orderNr = 13,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate.minusDays(3),
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUser = OutputUser(4L, "savePaymentInfo@User", "name", "surname"),
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUser = OutputUser(5L, "paymentConfirmed@User", "name", "surname"),
            paymentConfirmedDate = currentDate,
            correction = null,
        )
        private val installmentUpdate = PaymentPartnerInstallmentUpdate(
            id = 3L,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "comment",
            isSavePaymentInfo = true,
            savePaymentInfoUserId = 4L,
            savePaymentDate = currentDate,
            isPaymentConfirmed = true,
            paymentConfirmedUserId = 5L,
            paymentConfirmedDate = currentDate,
            correctionId = 15L,
        )
        private val expectedFund = ProgrammeFund(
            id = fundId,
            selected = true,
            type = ProgrammeFundType.OTHER,
            abbreviation = setOf(InputTranslation(SystemLanguage.ES, "fund ES abbr")),
            description = setOf(InputTranslation(SystemLanguage.ES, "fund ES desc")),
        )

        private val paymentFTLSToCreateMap = mapOf(
            PaymentGroupingId(7, fundId) to
                    PaymentFtlsToCreate(
                        lumpSumId,
                        listOf(
                            PaymentPartnerToCreate(partnerId_5, null, BigDecimal.valueOf(35), "abbr 5", "original 5", "english 5"),
                            PaymentPartnerToCreate(partnerId_6, null, BigDecimal.valueOf(65), null, null, null),
                        ),
                        amountApprovedPerFund = BigDecimal.valueOf(100), "proj-iden", "proj-acr",
                        defaultPartnerContribution = BigDecimal.valueOf(32),
                        defaultOfWhichPublic = BigDecimal.valueOf(33),
                        defaultOfWhichAutoPublic = BigDecimal.valueOf(34),
                        defaultOfWhichPrivate = BigDecimal.valueOf(35),
                        defaultTotalEligibleWithoutSco = BigDecimal.ZERO,
                        defaultFundAmountUnionContribution = BigDecimal.ZERO,
                        defaultFundAmountPublicContribution = BigDecimal.ZERO,
                    ),
        )

        private val expectedPayments = PaymentToProject(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectId = dummyProject.id,
            projectCustomIdentifier = dummyProject.customIdentifier,
            projectAcronym = "Test Project",
            paymentClaimId = null,
            paymentClaimNo = 0,
            paymentToEcId = null,
            fund = expectedFund,
            lumpSumId = 50L,
            orderNr = 13,
            fundAmount = BigDecimal(100),
            amountPaidPerFund = BigDecimal.ZERO,
            amountAuthorizedPerFund = BigDecimal.ZERO,
            paymentApprovalDate = currentTime,
            paymentClaimSubmissionDate = weekBefore,
            totalEligibleAmount = BigDecimal.TEN,
            dateOfLastPayment = null,
            lastApprovedVersionBeforeReadyForPayment = "V4.7",
            remainingToBePaid = BigDecimal.valueOf(100L),
        )
        val programmeLumpSum = programmeLumpSum(id = 50)


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

        val leadPartner = projectPartnerEntity(partnerId_5, abbreviation = "A", role = ProjectPartnerRole.LEAD_PARTNER, sortNumber = 2)
        val partner = projectPartnerEntity(partnerId_6, abbreviation = "B", role = ProjectPartnerRole.PARTNER, sortNumber = 2)

        private val dummyFilters = PaymentSearchRequest(
            paymentId = 855L,
            paymentType = PaymentType.FTLS,
            projectIdentifiers = setOf("472", "INT00473", ""),
            projectAcronym = "acr-filter",
            claimSubmissionDateFrom = null, // we do not test because it is hard to verify without calling toString on those dates
            claimSubmissionDateTo = null, // we do not test because it is hard to verify without calling toString on those dates
            approvalDateFrom = null, // we do not test because it is hard to verify without calling toString on those dates
            approvalDateTo = null, // we do not test because it is hard to verify without calling toString on those dates
            fundIds = setOf(511L, 512L),
            lastPaymentDateFrom = currentDate.minusDays(1),
            lastPaymentDateTo = currentDate.minusDays(1),
            ecPaymentIds = setOf(null, 693L),
            contractingScoBasis = PaymentSearchRequestScoBasis.FallsUnderArticle94Or95,
            finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
        )

        private val expectedFtlsPayment = PaymentToProject(
            id = paymentId,
            paymentType = PaymentType.FTLS,
            projectId = projectId,
            projectCustomIdentifier = "proj-cust",
            projectAcronym = "proj-acr",
            paymentClaimId = null,
            paymentClaimNo = 0,
            paymentClaimSubmissionDate = weekBefore,
            paymentToEcId = 14L,
            lumpSumId = 50L,
            orderNr = 13,
            paymentApprovalDate = currentTime,
            totalEligibleAmount = BigDecimal.valueOf(23L),
            fund = expectedFund,
            fundAmount = BigDecimal(100),
            amountAuthorizedPerFund = BigDecimal.valueOf(22),
            amountPaidPerFund = BigDecimal.valueOf(21),
            dateOfLastPayment = currentDate.plusDays(1),
            lastApprovedVersionBeforeReadyForPayment = "V4.7",
            remainingToBePaid = BigDecimal.valueOf(24L),
        )

        private val expectedRegularPayment = expectedFtlsPayment.copy(
            id = paymentId,
            paymentType = PaymentType.REGULAR,
            projectId = projectId,
            projectCustomIdentifier = "proj-cust",
            projectAcronym = "proj-acr",
            paymentClaimId = 777L,
            paymentClaimNo = 23,
            paymentClaimSubmissionDate = yesterday,
            paymentToEcId = null,
            lumpSumId = null,
            orderNr = null,
            paymentApprovalDate = yearAgo,
            totalEligibleAmount = BigDecimal.valueOf(13),
            fund = expectedFund,
            fundAmount = BigDecimal(100),
            amountAuthorizedPerFund = BigDecimal.valueOf(12),
            amountPaidPerFund = BigDecimal.valueOf(11),
            dateOfLastPayment = currentDate,
            lastApprovedVersionBeforeReadyForPayment = null,
            remainingToBePaid = BigDecimal.valueOf(14L),
        )


        // regular payments

        val regularPayments = mapOf(
            1L to PaymentRegularToCreate(
                projectId = projectId,
                partnerPayments = listOf(
                    PaymentPartnerToCreate(
                        partnerId = partnerId_6,
                        partnerReportId = 106,
                        amountApprovedPerPartner = BigDecimal.valueOf(800_00L, 2),
                        null, null, null,
                    ), PaymentPartnerToCreate(
                        partnerId = partnerId_5,
                        partnerReportId = 107,
                        amountApprovedPerPartner = BigDecimal.valueOf(400_00L, 2),
                        null, null, null,
                    ), PaymentPartnerToCreate(
                        partnerId = partnerId_5,
                        partnerReportId = 108,
                        amountApprovedPerPartner = BigDecimal.valueOf(400_00L, 2),
                        null, null, null,
                    )
                ),
                defaultPartnerContribution = BigDecimal.valueOf(50),
                defaultOfWhichPublic = BigDecimal.valueOf(75),
                defaultOfWhichAutoPublic = BigDecimal.valueOf(150),
                defaultOfWhichPrivate = BigDecimal.valueOf(40),
                amountApprovedPerFund = BigDecimal.valueOf(90_00L, 2),
                defaultTotalEligibleWithoutSco = BigDecimal.valueOf(210),
                defaultFundAmountUnionContribution = BigDecimal.valueOf(99.15),
                defaultFundAmountPublicContribution = BigDecimal.valueOf(75.00),
            ),
            4L to PaymentRegularToCreate(
                projectId = projectId,
                partnerPayments = listOf(
                    PaymentPartnerToCreate(
                        partnerId = partnerId_5,
                        partnerReportId = 107,
                        amountApprovedPerPartner = BigDecimal.valueOf(90_00L, 2),
                        null, null, null,
                    ), PaymentPartnerToCreate(
                        partnerId = partnerId_5,
                        partnerReportId = 108,
                        amountApprovedPerPartner = BigDecimal.valueOf(90_00L, 2),
                        null, null, null,
                    )
                ),
                defaultPartnerContribution = BigDecimal.valueOf(100),
                defaultOfWhichPublic = BigDecimal.valueOf(75),
                defaultOfWhichAutoPublic = BigDecimal.valueOf(300),
                defaultOfWhichPrivate = BigDecimal.valueOf(80),
                amountApprovedPerFund = BigDecimal.valueOf(180_00L, 2),
                defaultTotalEligibleWithoutSco = BigDecimal.ZERO,
                defaultFundAmountUnionContribution = BigDecimal.ZERO,
                defaultFundAmountPublicContribution = BigDecimal.ZERO,
            )
        )
    }

    @BeforeEach
    fun reset() {
        clearMocks(
            reportFileRepository, fileRepository, paymentRepository, paymentPartnerInstallmentRepository,
            paymentContributionMetaRepository, jpaQueryFactory, projectReportRepository,
            projectReportCoFinancingRepository, projectPartnerReportRepository, projectPartnerRepository, auditControlCorrectionRepository
        )
    }

    @Test
    fun getAllPaymentToProject() {
        val query = mockk<JPAQuery<Tuple>>()
        every {
            jpaQueryFactory.select(
                any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(),
                any(),
            )
        } returns query
        val slotFrom = slot<EntityPath<Any>>()
        every { query.from(capture(slotFrom)) } returns query
        val slotLeftJoin = mutableListOf<EntityPath<Any>>()
        every { query.leftJoin(capture(slotLeftJoin)) } returns query
        val slotLeftJoinOn = mutableListOf<BooleanOperation>()
        every { query.on(capture(slotLeftJoinOn)) } returns query
        val slotWhere = slot<BooleanOperation>()
        every { query.where(capture(slotWhere)) } returns query
        every { query.groupBy(any()) } returns query
        val slotHaving = slot<BooleanOperation>()
        every { query.having(capture(slotHaving)) } returns query
        val slotOffset = slot<Long>()
        every { query.offset(capture(slotOffset)) } returns query
        val slotLimit = slot<Long>()
        every { query.limit(capture(slotLimit)) } returns query
        val slotOrderBy = slot<OrderSpecifier<*>>()
        every { query.orderBy(capture(slotOrderBy)) } returns query

        val tupleFtls = mockk<Tuple>()
        every { tupleFtls.get(0, PaymentEntity::class.java) } returns paymentFtlsEntity()
        every { tupleFtls.get(1, BigDecimal::class.java) } returns BigDecimal.valueOf(21) // amount confirmed (paid)
        every { tupleFtls.get(2, BigDecimal::class.java) } returns BigDecimal.valueOf(22) // amount authorized
        every { tupleFtls.get(3, LocalDate::class.java) } returns currentDate.plusDays(1)
        every { tupleFtls.get(4, BigDecimal::class.java) } returns BigDecimal.valueOf(23)
        every { tupleFtls.get(5, BigDecimal::class.java) } returns BigDecimal.valueOf(24)
        every { tupleFtls.get(6, String::class.java) } returns "PO4"

        every { tupleFtls.get(7, Long::class.java) } returns 14L
        every { tupleFtls.get(8, BigDecimal::class.java) } returns BigDecimal.valueOf(21)
        every { tupleFtls.get(9, BigDecimal::class.java) } returns BigDecimal.valueOf(22)
        every { tupleFtls.get(10, BigDecimal::class.java) } returns BigDecimal.valueOf(23)
        every { tupleFtls.get(11, BigDecimal::class.java) } returns BigDecimal.valueOf(24)
        every { tupleFtls.get(12, BigDecimal::class.java) } returns BigDecimal.valueOf(25)
        every { tupleFtls.get(13, BigDecimal::class.java) } returns BigDecimal.valueOf(26)
        every { tupleFtls.get(14, BigDecimal::class.java) } returns BigDecimal.valueOf(27)
        every { tupleFtls.get(15, BigDecimal::class.java) } returns BigDecimal.valueOf(28)
        every { tupleFtls.get(16, BigDecimal::class.java) } returns BigDecimal.valueOf(29)
        every { tupleFtls.get(17, BigDecimal::class.java) } returns BigDecimal.valueOf(30)
        every { tupleFtls.get(18, String::class.java) } returns "comment"

        val tupleRegular = mockk<Tuple>()
        every { tupleRegular.get(0, PaymentEntity::class.java) } returns paymentRegularEntity()
        every { tupleRegular.get(1, BigDecimal::class.java) } returns BigDecimal.valueOf(11) // amount confirmed (paid)
        every { tupleRegular.get(2, BigDecimal::class.java) } returns BigDecimal.valueOf(12) // amount authorized
        every { tupleRegular.get(3, LocalDate::class.java) } returns currentDate
        every { tupleRegular.get(4, BigDecimal::class.java) } returns BigDecimal.valueOf(13)
        every { tupleRegular.get(5, BigDecimal::class.java) } returns BigDecimal.valueOf(14)
        every { tupleRegular.get(6, String::class.java) } returns "SO15"

        every { tupleRegular.get(7, Long::class.java) } returns null
        every { tupleRegular.get(8, BigDecimal::class.java) } returns BigDecimal.valueOf(31)
        every { tupleRegular.get(9, BigDecimal::class.java) } returns BigDecimal.valueOf(32)
        every { tupleRegular.get(10, BigDecimal::class.java) } returns BigDecimal.valueOf(33)
        every { tupleRegular.get(11, BigDecimal::class.java) } returns BigDecimal.valueOf(34)
        every { tupleRegular.get(12, BigDecimal::class.java) } returns BigDecimal.valueOf(35)
        every { tupleRegular.get(13, BigDecimal::class.java) } returns BigDecimal.valueOf(36)
        every { tupleRegular.get(14, BigDecimal::class.java) } returns BigDecimal.valueOf(37)
        every { tupleRegular.get(15, BigDecimal::class.java) } returns BigDecimal.valueOf(38)
        every { tupleRegular.get(16, BigDecimal::class.java) } returns BigDecimal.valueOf(39)
        every { tupleRegular.get(17, BigDecimal::class.java) } returns BigDecimal.valueOf(49)
        every { tupleRegular.get(18, String::class.java) } returns "comment"

        val result = mockk<QueryResults<Tuple>>()
        every { result.total } returns 2
        every { result.results } returns listOf(tupleFtls, tupleRegular)
        every { query.fetchResults() } returns result

        assertThat(paymentPersistenceProvider.getAllPaymentToProject(Pageable.ofSize(5), dummyFilters).content)
            .containsExactly(expectedFtlsPayment, expectedRegularPayment)

        assertThat(slotFrom.captured).isInstanceOf(QPaymentEntity::class.java)
        assertThat(slotLeftJoin[0]).isInstanceOf(QPaymentPartnerEntity::class.java)
        assertThat(slotLeftJoinOn[0].toString()).isEqualTo("paymentPartnerEntity.payment = paymentEntity")
        assertThat(slotLeftJoin[1]).isInstanceOf(QPaymentPartnerInstallmentEntity::class.java)
        assertThat(slotLeftJoinOn[1].toString()).isEqualTo("paymentPartnerInstallmentEntity.paymentPartner = paymentPartnerEntity")
        assertThat(slotLeftJoin[2]).isInstanceOf(QProjectLumpSumEntity::class.java)
        assertThat(slotLeftJoinOn[2].toString()).isEqualTo("projectLumpSumEntity = paymentEntity.projectLumpSum")
        assertThat(slotLeftJoin[3]).isInstanceOf(QProjectReportEntity::class.java)
        assertThat(slotLeftJoinOn[3].toString()).isEqualTo("projectReportEntity = paymentEntity.projectReport")
        assertThat(slotLeftJoin[4]).isInstanceOf(QProjectContractingMonitoringEntity::class.java)
        assertThat(slotLeftJoinOn[4].toString()).isEqualTo("projectContractingMonitoringEntity.projectId = paymentEntity.project.id")
        assertThat(slotLeftJoin[5]).isInstanceOf(QProjectEntity::class.java)
        assertThat(slotLeftJoinOn[5].toString()).isEqualTo("projectEntity = paymentEntity.project")
        assertThat(slotLeftJoin[6]).isInstanceOf(QProgrammeSpecificObjectiveEntity::class.java)
        assertThat(slotLeftJoinOn[6].toString())
            .isEqualTo("programmeSpecificObjectiveEntity.programmeObjectivePolicy = projectEntity.priorityPolicy.programmeObjectivePolicy")
        assertThat(slotLeftJoin[7]).isInstanceOf(QProgrammePriorityEntity::class.java)
        assertThat(slotLeftJoinOn[7].toString()).isEqualTo("programmePriorityEntity = programmeSpecificObjectiveEntity.programmePriority")
        assertThat(slotLeftJoin[8]).isInstanceOf(QPaymentToEcExtensionEntity::class.java)
        assertThat(slotLeftJoinOn[8].toString()).isEqualTo("paymentToEcExtensionEntity.payment = paymentEntity")
        assertThat(slotWhere.captured.toString()).isEqualTo(
            "paymentEntity.id = 855 " +
                    "&& paymentEntity.type = FTLS " +
                    "&& (paymentEntity.project.id = 472 || paymentEntity.projectCustomIdentifier in [472, INT00473]) " +
                    "&& containsIc(paymentEntity.projectAcronym,acr-filter) " +
                    "&& paymentEntity.fund.id in [511, 512] " +
                    "&& (paymentToEcExtensionEntity.paymentApplicationToEc is null || paymentToEcExtensionEntity.paymentApplicationToEc.id = 693) " +
                    "&& !((projectContractingMonitoringEntity.typologyProv94 is null || projectContractingMonitoringEntity.typologyProv94 = No) " +
                    "&& (projectContractingMonitoringEntity.typologyProv95 is null || projectContractingMonitoringEntity.typologyProv95 = No)) " +
                    "&& paymentToEcExtensionEntity.finalScoBasis = DoesNotFallUnderArticle94Nor95"
        )
        assertThat(slotHaving.captured.toString()).isEqualTo(
            "max(paymentPartnerInstallmentEntity.paymentDate) >= 2023-07-10 " +
                    "&& max(paymentPartnerInstallmentEntity.paymentDate) <= 2023-07-10"
        )
        assertThat(slotOffset.captured).isEqualTo(0L)
        assertThat(slotLimit.captured).isEqualTo(5L)
        assertThat(slotOrderBy.captured.order).isEqualTo(Order.DESC)
        assertThat(slotOrderBy.captured.target.toString()).isEqualTo("paymentEntity.id")
    }

    @Test
    fun getConfirmedInfosForPayment() {
        val payment = partnerPaymentEntity()
        every {
            paymentPartnerInstallmentRepository.findAllByPaymentPartnerPaymentId(paymentId)
        } returns listOf(
            installmentEntity(), PaymentPartnerInstallmentEntity(
                id = 4L,
                paymentPartner = payment,
                amountPaid = BigDecimal("22.21"),
                paymentDate = currentDate.minusDays(1),
                comment = "comment",
                isSavePaymentInfo = true,
                savePaymentInfoUser = savePaymentUser,
                savePaymentDate = currentDate,
                isPaymentConfirmed = false,
                paymentConfirmedUser = null,
                paymentConfirmedDate = null,
                correction = null,
            ),
            PaymentPartnerInstallmentEntity(
                id = 5L,
                paymentPartner = payment,
                amountPaid = BigDecimal("22.21"),
                paymentDate = currentDate.minusDays(5),
                comment = "comment",
                isSavePaymentInfo = true,
                savePaymentInfoUser = savePaymentUser,
                savePaymentDate = currentDate,
                isPaymentConfirmed = true,
                paymentConfirmedUser = paymentConfirmedUser,
                paymentConfirmedDate = currentDate.minusDays(4),
                correction = null,
            ),
            PaymentPartnerInstallmentEntity(
                id = 6L,
                paymentPartner = payment,
                amountPaid = BigDecimal("-5.1"),
                paymentDate = currentDate.minusDays(2),
                comment = "comment",
                isSavePaymentInfo = true,
                savePaymentInfoUser = savePaymentUser,
                savePaymentDate = currentDate,
                isPaymentConfirmed = true,
                paymentConfirmedUser = paymentConfirmedUser,
                paymentConfirmedDate = currentDate.plusDays(2),
                correction = null,
            )
        )

        assertThat(paymentPersistenceProvider.getConfirmedInfosForPayment(paymentId)).isEqualTo(
            PaymentConfirmedInfo(
                id = paymentId,
                amountPaidPerFund = BigDecimal("27.11"),
                amountAuthorizedPerFund = BigDecimal("49.32"),
                dateOfLastPayment = currentDate.minusDays(2)
            )
        )
    }

    @Test
    fun deleteAllByProjectIdAndOrderNrIn() {
        every {
            paymentRepository.deleteAllByProjectIdAndProjectLumpSumIdOrderNrInAndType(projectId, setOf(1), PaymentType.FTLS)
        } answers { }
        paymentPersistenceProvider.deleteFTLSByProjectIdAndOrderNrIn(projectId, setOf(1))
        verify(exactly = 1) {
            paymentRepository.deleteAllByProjectIdAndProjectLumpSumIdOrderNrInAndType(projectId, setOf(1), PaymentType.FTLS)
        }
    }

    @Test
    fun saveFTLSPaymentToProjects() {
        val project = mockk<ProjectEntity>()
        every { projectRepository.getReferenceById(projectId) } returns project
        val fund = mockk<ProgrammeFundEntity>()
        every { fundRepository.getReferenceById(any()) } returns fund
        every { fund.id } returns fundId
        val lumpSum = mockk<ProjectLumpSumEntity>()
        every { projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(projectId, 7) } returns lumpSum
        every { lumpSum.id } returns ProjectLumpSumId(projectId, 7)

        val slotPayments = slot<MutableList<PaymentEntity>>()
        every { paymentRepository.saveAll(capture(slotPayments)) } returnsArgument 0

        val leadPartner = projectPartnerEntity
        val partner = projectPartnerEntity(partnerId_6, abbreviation = "B", role = ProjectPartnerRole.PARTNER, sortNumber = 2)

        every { projectPartnerRepository.getReferenceById(partnerId_5) } returns leadPartner
        every { projectPartnerRepository.getReferenceById(partnerId_6) } returns partner


        val slotPartners = slot<MutableList<PaymentPartnerEntity>>()
        every { paymentPartnerRepository.saveAll(capture(slotPartners)) } returnsArgument 0
        val slotExtension = slot<PaymentToEcExtensionEntity>()
        every { paymentToEcExtensionRepository.save(capture(slotExtension)) } returnsArgument 0

        paymentPersistenceProvider.saveFTLSPayments(projectId, paymentFTLSToCreateMap)

        with(slotPayments.captured) {
            assertThat(get(0).id).isEqualTo(0L)
            assertThat(get(0).type).isEqualTo(PaymentType.FTLS)
            assertThat(get(0).project).isEqualTo(project)
            assertThat(get(0).projectCustomIdentifier).isEqualTo("proj-iden")
            assertThat(get(0).projectAcronym).isEqualTo("proj-acr")
            assertThat(get(0).projectLumpSum).isEqualTo(lumpSum)
            assertThat(get(0).fund).isEqualTo(fund)
            assertThat(get(0).amountApprovedPerFund).isEqualTo(BigDecimal.valueOf(100))
        }
        with(slotPartners.captured) {
            assertThat(get(0).id).isEqualTo(0L)
            assertThat(get(0).payment).isEqualTo(slotPayments.captured.first())
            assertThat(get(0).projectPartner).isEqualTo(leadPartner)
            assertThat(get(0).amountApprovedPerPartner).isEqualTo(BigDecimal.valueOf(35))
            assertThat(get(1).id).isEqualTo(0L)
            assertThat(get(1).payment).isEqualTo(slotPayments.captured.first())
            assertThat(get(1).projectPartner).isEqualTo(partner)
            assertThat(get(1).amountApprovedPerPartner).isEqualTo(BigDecimal.valueOf(65))
        }
        with(slotExtension.captured) {
            assertThat(paymentId).isEqualTo(0L)
            assertThat(paymentApplicationToEc).isNull()
            assertThat(partnerContribution).isEqualTo(BigDecimal.valueOf(32))
            assertThat(publicContribution).isEqualTo(BigDecimal.valueOf(33))
            assertThat(correctedPublicContribution).isEqualTo(BigDecimal.valueOf(33))
            assertThat(autoPublicContribution).isEqualTo(BigDecimal.valueOf(34))
            assertThat(correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(34))
            assertThat(privateContribution).isEqualTo(BigDecimal.valueOf(35))
            assertThat(correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(35))
        }
    }

    @Test
    fun saveRegularPaymentToProjects() {
        val project = mockk<ProjectEntity>()
        every { projectRepository.getReferenceById(projectId) } returns project


        every { projectReportRepository.getReferenceById(projectReportId) } returns
                projectReportEntity(projectReportId, projectIdentifier = "proj-iden", projectAcronym = "proj-acr")

        val fundERDFEntity = ERDF_FUND.toEntity()
        val fundIPAEntity = IPA_III_FUND.toEntity()
        val projectReportCoFin1 = mockk<ProjectReportCoFinancingEntity>()
        every { projectReportCoFin1.programmeFund } returns fundERDFEntity
        val projectReportCoFin2 = mockk<ProjectReportCoFinancingEntity>()
        every { projectReportCoFin2.programmeFund } returns fundIPAEntity
        every { projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(projectReportId) } returns listOf(
            projectReportCoFin1, projectReportCoFin2
        )

        val slotPayments = slot<List<PaymentEntity>>()
        every { paymentRepository.saveAll(capture(slotPayments)) } answers { slotPayments.captured }


        every { projectPartnerRepository.getReferenceById(partnerId_5) } returns leadPartner
        every { projectPartnerRepository.getReferenceById(partnerId_6) } returns partner


        val leadPartnerR1 = mockk<ProjectPartnerReportEntity>()
        every { leadPartnerR1.id } returns 107L
        every { leadPartnerR1.partnerId } returns 5L

        val leadPartnerR2 = mockk<ProjectPartnerReportEntity>()
        every { leadPartnerR2.id } returns 108L
        every { leadPartnerR2.partnerId } returns 5L

        val secondPartnerR3 = mockk<ProjectPartnerReportEntity>()
        every { secondPartnerR3.id } returns 106L
        every { secondPartnerR3.partnerId } returns 6L

        every { projectPartnerReportRepository.getReferenceById(107L) } returns leadPartnerR1
        every { projectPartnerReportRepository.getReferenceById(108L) } returns leadPartnerR2
        every { projectPartnerReportRepository.getReferenceById(106L) } returns secondPartnerR3
        val slotExtensions = mutableListOf<PaymentToEcExtensionEntity>()
        every { paymentToEcExtensionRepository.save(capture(slotExtensions)) } returnsArgument 0

        val slotPartners = slot<List<PaymentPartnerEntity>>()
        every { paymentPartnerRepository.saveAll(capture(slotPartners)) } answers { slotPartners.captured }

        paymentPersistenceProvider.saveRegularPayments(projectReportId, regularPayments)

        with(slotPayments.captured) {
            assertThat(get(0).id).isEqualTo(0L)
            assertThat(get(0).type).isEqualTo(PaymentType.REGULAR)
            assertThat(get(0).project).isEqualTo(project)
            assertThat(get(0).projectCustomIdentifier).isEqualTo("proj-iden")
            assertThat(get(0).projectAcronym).isEqualTo("proj-acr")
            assertThat(get(0).projectLumpSum).isNull()
            assertThat(get(0).projectReport).isNotNull
            assertThat(get(0).fund).isEqualTo(fundERDFEntity)
            assertThat(get(0).amountApprovedPerFund).isEqualTo(BigDecimal.valueOf(90_00L, 2))
        }
        with(slotPayments.captured) {
            assertThat(get(1).id).isEqualTo(0L)
            assertThat(get(1).type).isEqualTo(PaymentType.REGULAR)
            assertThat(get(1).project).isEqualTo(project)
            assertThat(get(1).projectCustomIdentifier).isEqualTo("proj-iden")
            assertThat(get(1).projectAcronym).isEqualTo("proj-acr")
            assertThat(get(1).projectLumpSum).isNull()
            assertThat(get(1).projectReport).isNotNull
            assertThat(get(1).fund).isEqualTo(fundIPAEntity)
            assertThat(get(1).amountApprovedPerFund).isEqualTo(BigDecimal.valueOf(180_00L, 2))
        }

        assertThat(slotPartners.captured.size).isEqualTo(2)
        with(slotPartners.captured) {

            assertThat(get(0).payment).isEqualTo(slotPayments.captured.get(1))
            assertThat(get(0).projectPartner).isEqualTo(leadPartner)
            assertThat(get(0).partnerCertificate).isEqualTo(leadPartnerR1)
            assertThat(get(0).partnerAbbreviation).isEqualTo("")
            assertThat(get(0).partnerNameInOriginalLanguage).isEqualTo("")
            assertThat(get(0).partnerNameInEnglish).isEqualTo("")

            assertThat(get(1).payment).isEqualTo(slotPayments.captured.get(1))
            assertThat(get(1).projectPartner).isEqualTo(leadPartner)
            assertThat(get(1).partnerCertificate).isEqualTo(leadPartnerR2)
            assertThat(get(1).partnerAbbreviation).isEqualTo("")
            assertThat(get(1).partnerNameInOriginalLanguage).isEqualTo("")
            assertThat(get(1).partnerNameInEnglish).isEqualTo("")
        }

        with(slotExtensions[0]) {
            assertThat(paymentId).isEqualTo(0L)
            assertThat(paymentApplicationToEc).isNull()
            assertThat(partnerContribution).isEqualTo(BigDecimal.valueOf(50))
            assertThat(publicContribution).isEqualTo(BigDecimal.valueOf(75))
            assertThat(correctedPublicContribution).isEqualTo(BigDecimal.valueOf(75))
            assertThat(autoPublicContribution).isEqualTo(BigDecimal.valueOf(150))
            assertThat(correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(150))
            assertThat(privateContribution).isEqualTo(BigDecimal.valueOf(40))
            assertThat(correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(40))
            assertThat(correctedTotalEligibleWithoutSco).isEqualTo( BigDecimal.valueOf(210))
            assertThat(correctedFundAmountUnionContribution).isEqualTo(BigDecimal.valueOf(99.15))
            assertThat(correctedFundAmountPublicContribution).isEqualTo(BigDecimal.valueOf(75.00))
        }
        with(slotExtensions[1]) {
            assertThat(paymentId).isEqualTo(0L)
            assertThat(paymentApplicationToEc).isNull()
            assertThat(partnerContribution).isEqualTo(BigDecimal.valueOf(100))
            assertThat(publicContribution).isEqualTo(BigDecimal.valueOf(75))
            assertThat(correctedPublicContribution).isEqualTo(BigDecimal.valueOf(75))
            assertThat(autoPublicContribution).isEqualTo(BigDecimal.valueOf(300))
            assertThat(correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(300))
            assertThat(privateContribution).isEqualTo(BigDecimal.valueOf(80))
            assertThat(correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(80))
            assertThat(correctedTotalEligibleWithoutSco).isEqualTo(BigDecimal.ZERO)
            assertThat(correctedFundAmountUnionContribution).isEqualTo(BigDecimal.ZERO)
            assertThat(correctedFundAmountPublicContribution).isEqualTo(BigDecimal.ZERO)
        }

    }

    @Test
    fun getAllPartnerPaymentsForPartner() {
        every { paymentPartnerRepository.findAllByProjectPartnerId(22L) } returns listOf(partnerPaymentEntity())
        assertThat(paymentPersistenceProvider.getAllPartnerPaymentsForPartner(22L))
            .containsExactly(PartnerPaymentSimple(fundId, BigDecimal.ONE))
    }

    @Test
    fun getPaymentPartnerId() {
        every { paymentPartnerRepository.getIdByPaymentIdAndPartnerId(78L, 420L) } returns 22L
        assertThat(paymentPersistenceProvider.getPaymentPartnerId(78L, 420L)).isEqualTo(22L)
    }

    @Test
    fun updatePaymentPartnerInstallments() {
        val deleteIds = setOf(2L)
        every { paymentPartnerInstallmentRepository.deleteAllByIdInBatch(deleteIds) } returns Unit
        every { paymentPartnerRepository.getReferenceById(1L) } returns partnerPaymentEntity()
        every { userRepository.getReferenceById(paymentConfirmedUser.id) } returns paymentConfirmedUser
        every { userRepository.getReferenceById(savePaymentUser.id) } returns savePaymentUser
        every {
            paymentPartnerInstallmentRepository.saveAll(any<List<PaymentPartnerInstallmentEntity>>())
        } returns listOf(installmentEntity())
        every { auditControlCorrectionRepository.getReferenceById(15L) } returns mockk { every { id } returns 15L }

        assertThat(
            paymentPersistenceProvider.updatePaymentPartnerInstallments(
                paymentPartnerId = 1L,
                toDeleteInstallmentIds = deleteIds,
                paymentPartnerInstallments = listOf(installmentUpdate)
            )
        ).containsExactly(installmentFirst)
    }

    @Test
    fun findByPartnerId() {
        every { paymentPartnerInstallmentRepository.findAllByPaymentPartnerProjectPartnerId(64L) } returns
                listOf(installmentEntity())
        assertThat(paymentPersistenceProvider.findByPartnerId(64L)).containsExactly(installmentFirst)
    }

    @Test
    fun deletePaymentAttachment() {
        val file = mockk<JemsFileMetadataEntity>()
        every { fileRepository.delete(file) } answers { }
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentAttachment, 14L) } returns file
        paymentPersistenceProvider.deletePaymentAttachment(14L)
        verify(exactly = 1) { fileRepository.delete(file) }
    }

    @Test
    fun `deletePaymentAttachment - not existing`() {
        every { reportFileRepository.findByTypeAndId(JemsFileType.PaymentAttachment, -1L) } returns null
        assertThrows<ResourceNotFoundException> { paymentPersistenceProvider.deletePaymentAttachment(-1L) }
        verify(exactly = 0) { fileRepository.delete(any()) }
    }

    @Test
    fun getPaymentsByProjectIdToProject() {
        every { paymentRepository.findAllByProjectId(projectId) } returns mutableListOf(paymentFtlsEntity())

        assertThat(paymentPersistenceProvider.getPaymentsByProjectId(projectId))
            .containsExactly(expectedPayments.copy(projectCustomIdentifier = "proj-cust", projectAcronym = "proj-acr"))
    }

    @Test
    fun storePartnerContributionsWhenReadyForPayment() {
        val saved = slot<Iterable<PaymentContributionMetaEntity>>()
        every { paymentContributionMetaRepository.saveAll(capture(saved)) } returnsArgument 0
        val toSave = listOf(
            ContributionMeta(
                projectId = 4L,
                partnerId = 8L,
                programmeLumpSumId = 15L,
                orderNr = 10,
                partnerContribution = BigDecimal.valueOf(100L),
                publicContribution = BigDecimal.valueOf(200L),
                automaticPublicContribution = BigDecimal.valueOf(300L),
                privateContribution = BigDecimal.valueOf(400L),
            )
        )
        paymentPersistenceProvider.storePartnerContributionsWhenReadyForPayment(toSave)

        assertThat(saved.captured).hasSize(1)
        with(saved.captured.first()) {
            assertThat(id).isEqualTo(0L)
            assertThat(projectId).isEqualTo(4L)
            assertThat(partnerId).isEqualTo(8L)
            assertThat(lumpSum.programmeLumpSumId).isEqualTo(15L)
            assertThat(lumpSum.orderNr).isEqualTo(10)
            assertThat(partnerContribution).isEqualByComparingTo(BigDecimal.valueOf(100L))
            assertThat(publicContribution).isEqualByComparingTo(BigDecimal.valueOf(200L))
            assertThat(automaticPublicContribution).isEqualByComparingTo(BigDecimal.valueOf(300L))
            assertThat(privateContribution).isEqualByComparingTo(BigDecimal.valueOf(400L))
        }
    }

    @Test
    fun deleteContributionsWhenReadyForPaymentReverted() {
        val toDelete = mockk<List<PaymentContributionMetaEntity>>()
        every { paymentContributionMetaRepository.findByProjectIdAndLumpSumOrderNrIn(140L, setOf(7, 8)) } returns toDelete
        every { paymentContributionMetaRepository.deleteAll(toDelete) } answers { }
        paymentPersistenceProvider.deleteContributionsWhenReadyForPaymentReverted(140L, setOf(7, 8))
        verify(exactly = 1) { paymentContributionMetaRepository.deleteAll(toDelete) }
    }

    @Test
    fun getCoFinancingAndContributionsCumulative() {
        every { paymentContributionMetaRepository.getContributionCumulative(partnerId_5) } returns PartnerContributionSplit(
            partnerContribution = BigDecimal.valueOf(510L),
            publicContribution = BigDecimal.valueOf(150L),
            automaticPublicContribution = BigDecimal.valueOf(170L),
            privateContribution = BigDecimal.valueOf(190L),
        )
        every { paymentPartnerRepository.getPaymentOfTypeCumulativeForPartner(PaymentType.FTLS, partnerId_5) } returns listOf(
            Pair(45L, BigDecimal.valueOf(750L)),
            Pair(46L, BigDecimal.valueOf(347L)),
        )
        assertThat(paymentPersistenceProvider.getFtlsCumulativeForPartner(partnerId_5)).isEqualTo(
            ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    45L to BigDecimal.valueOf(750L),
                    46L to BigDecimal.valueOf(347L),
                    null to BigDecimal.valueOf(510L),
                ),
                partnerContribution = BigDecimal.valueOf(510L),
                publicContribution = BigDecimal.valueOf(150L),
                automaticPublicContribution = BigDecimal.valueOf(170L),
                privateContribution = BigDecimal.valueOf(190L),
                sum = BigDecimal.valueOf(1607L),
            )
        )
    }

    @Test
    fun getPaymentsCumulativeForProject() {
        every { paymentContributionMetaRepository.getContributionCumulativePerProject(projectId) } returns PartnerContributionSplit(
            partnerContribution = BigDecimal.valueOf(810L),
            publicContribution = BigDecimal.valueOf(250L),
            automaticPublicContribution = BigDecimal.valueOf(270L),
            privateContribution = BigDecimal.valueOf(290L),
        )
        every { paymentPartnerRepository.getPaymentOfTypeCumulativeForProject(PaymentType.FTLS, projectId) } returns listOf(
            Pair(47L, BigDecimal.valueOf(650L)),
            Pair(48L, BigDecimal.valueOf(247L)),
        )
        every { paymentPartnerInstallmentRepository.getConfirmedCumulativeForProject(projectId) } returns listOf(
            Pair(47L, BigDecimal.valueOf(111L)),
            Pair(48L, BigDecimal.valueOf(333L)),
        )
        assertThat(paymentPersistenceProvider.getFtlsCumulativeForProject(projectId)).isEqualTo(
            PaymentCumulativeData(
                amounts = PaymentCumulativeAmounts(
                    funds = mapOf(
                        47L to BigDecimal.valueOf(650L),
                        48L to BigDecimal.valueOf(247L),
                    ),
                    partnerContribution = BigDecimal.valueOf(810L),
                    publicContribution = BigDecimal.valueOf(250L),
                    automaticPublicContribution = BigDecimal.valueOf(270L),
                    privateContribution = BigDecimal.valueOf(290L),
                ),
                confirmedAndPaid = mapOf(
                    47L to BigDecimal.valueOf(111L),
                    48L to BigDecimal.valueOf(333L),
                ),
            )
        )
    }

    @Test
    fun getPaymentIdsInstallmentsExistsByProjectReportId() {
        every { paymentPartnerInstallmentRepository.findAllByPaymentPartnerPaymentProjectReportId(projectReportId) } returns
                listOf(installmentEntity())
        assertThat(paymentPersistenceProvider.getPaymentIdsInstallmentsExistsByProjectReportId(projectReportId)).isEqualTo(
            setOf(paymentId)
        )
    }

    @Test
    fun deleteRegularPayments() {
        val file = mockk<JemsFileMetadataEntity>()
        val paymentRegularEntity = paymentRegularEntity()
        every { file.id } returns 18L
        every { paymentRepository.findAllByProjectReportId(projectReportId) } returns listOf(paymentRegularEntity)
        val fileList = listOf(file)
        every { reportFileRepository.findAllByPath("Payment/Regular/000002/PaymentAttachment/") } returns fileList
        every { fileRepository.deleteBatch(fileList) } returns Unit
        every { paymentRepository.delete(paymentRegularEntity) } returns Unit

        paymentPersistenceProvider.deleteRegularPayments(projectReportId)
        verify(exactly = 1) { fileRepository.deleteBatch(fileList) }
        verify(exactly = 1) { paymentRepository.delete(paymentRegularEntity) }
    }

    @Test
    fun getAvailableFtlsPayments() {
        val partnerIds = setOf(22L, 23L, 24L)

        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any(), any(), any()) } returns query
        every { query.from(QPaymentEntity.paymentEntity) } returns query
        every { query.leftJoin(QPaymentPartnerEntity.paymentPartnerEntity) } returns query
        every { query.leftJoin(QProjectLumpSumEntity.projectLumpSumEntity) } returns query
        every { query.leftJoin(QProgrammeFundEntity.programmeFundEntity) } returns query
        every { query.leftJoin(QPaymentToEcExtensionEntity.paymentToEcExtensionEntity) } returns query
        every { query.leftJoin(QPaymentApplicationToEcEntity.paymentApplicationToEcEntity) } returns query
        every { query.leftJoin(QAccountingYearEntity.accountingYearEntity) } returns query
        every { query.on(any()) } returns query
        val where = slot<Predicate>()
        every { query.where(capture(where)) } returns query
        val tupleFtls = mockk<Tuple> {
            every { get(0, Long::class.java) } returns 22L
            every { get(1, ProgrammeLumpSumEntity::class.java) } returns mockk {
                every { id } returns 23L
                every { translatedValues } returns mutableSetOf()
            }
            every { get(2, Int::class.java) } returns 8
            every { get(3, ProgrammeFundEntity::class.java) } returns fund
            every { get(4, Long::class.java) } returns 9L
            every { get(5, PaymentEcStatus::class.java) } returns PaymentEcStatus.Finished
            every { get(6, AccountingYearEntity::class.java) } returns null
        }
        every { query.fetch() } returns listOf(tupleFtls)

        assertThat(paymentPersistenceProvider.getAvailableFtlsPayments(partnerIds)).usingRecursiveComparison()
            .isEqualTo(
                listOf(
                    CorrectionAvailableFtlsTmp(
                        partnerId = 22,
                        programmeLumpSumId = 23L,
                        orderNr = 8,
                        name = setOf(),
                        availableFund = fund.toModel(),
                        ecPaymentId = 9L,
                        ecPaymentStatus = PaymentEcStatus.Finished,
                        ecPaymentAccountingYear = null
                    )
                )
            )

        assertThat(where.captured.toString())
            .isEqualTo("paymentPartnerEntity.projectPartner.id in [22, 23, 24] " +
                    "&& projectLumpSumEntity.isReadyForPayment = true " +
                    "&& projectLumpSumEntity.programmeLumpSum.isFastTrack = true")
    }

}
