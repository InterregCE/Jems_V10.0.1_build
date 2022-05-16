package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.ProjectReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportBaseData
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportBudget
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

internal class CreateProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 426L

        private const val WORK_PACKAGE_ID = 5658L
        private const val ACTIVITY_ID = 5942L
        private const val DELIVERABLE_ID = 5225L

        private fun projectSummary(status: ApplicationStatus) = ProjectFull(
            id = PROJECT_ID,
            customIdentifier = "XE.1_0001",
            callSettings = mockk(),
            acronym = "project acronym",
            applicant = mockk(),
            projectStatus = ProjectStatus(
                status = status,
                user = mockk(),
                updated = mockk(),
            ),
            duration = null,
        )

        private fun partnerDetail(id: Long, countryCode: String?) = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = id,
            active = true,
            abbreviation = "abbr",
            role = ProjectPartnerRole.PARTNER,
            sortNumber = 4,
            createdAt = ZonedDateTime.now(),
            nameInOriginalLanguage = "name in orig",
            nameInEnglish = "name in eng",
            partnerType = ProjectTargetGroup.SectoralAgency,
            partnerSubType = null,
            nace = null,
            otherIdentifierNumber = null,
            pic = "123456789",
            legalStatusId = 697854L,
            vat = "",
            addresses = listOf(
                ProjectPartnerAddress(
                    type = ProjectPartnerAddressType.Organization,
                    country = "Österreich (AT)",
                    countryCode = countryCode
                )
            ),
            vatRecovery = ProjectPartnerVatRecovery.Yes,
        )

        private val coFinancing = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = ProgrammeFund(
                    id = 7748L,
                    selected = true,
                    type = ProgrammeFundType.ERDF,
                ),
                percentage = BigDecimal.ONE,
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fund = null,
                percentage = BigDecimal.TEN,
            ),
        )

        private val contributions = listOf(
            ProjectPartnerContribution(
                id = 200L,
                name = "private id=200 amount=10",
                status = ProjectPartnerContributionStatusDTO.Private,
                amount = BigDecimal.TEN,
                isPartner = true,
            ),
        )

        private fun expectedCreationObject(partnerId: Long, budget: PartnerReportBudget) = ProjectPartnerReportCreate(
            baseData = PartnerReportBaseData(
                partnerId = partnerId,
                reportNumber = 7 + 1,
                status = ReportStatus.Draft,
                version = "14.2.0",
            ),
            identification = PartnerReportIdentificationCreate(
                projectIdentifier = "XE.1_0001",
                projectAcronym = "project acronym",
                partnerNumber = 4,
                partnerAbbreviation = "abbr",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "name in orig",
                nameInEnglish = "name in eng",
                legalStatusId = 697854L,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                country = "Österreich (AT)",
                countryCode = "AT",
                currency = "EUR",
                coFinancing = coFinancing
            ),
            workPackages = listOf(
                CreateProjectPartnerReportWorkPackage(
                    workPackageId = WORK_PACKAGE_ID,
                    number = 2,
                    activities = listOf(
                        CreateProjectPartnerReportWorkPackageActivity(
                            activityId = ACTIVITY_ID,
                            number = 1,
                            title = setOf(InputTranslation(EN, "4.1 activity title")),
                            deliverables = listOf(
                                CreateProjectPartnerReportWorkPackageActivityDeliverable(
                                    deliverableId = DELIVERABLE_ID,
                                    number = 1,
                                    title = setOf(InputTranslation(EN, "4.1.1 deliverable title")),
                                ),
                            ),
                        ),
                    ),
                    outputs = listOf(
                        CreateProjectPartnerReportWorkPackageOutput(
                            number = 7,
                            title = setOf(InputTranslation(EN, "7 output title")),
                        ),
                    ),
                ),
            ),
            targetGroups = listOf(
                ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.EducationTrainingCentreAndSchool,
                    specification = setOf(InputTranslation(EN, "EducationTrainingCentreAndSchool")),
                ),
            ),
            budget = budget,
        )

        private fun expectedCreationObjectLimited(partnerId: Long, budget: PartnerReportBudget) = ProjectPartnerReportCreate(
            baseData = PartnerReportBaseData(
                partnerId = partnerId,
                reportNumber = 7 + 1,
                status = ReportStatus.Draft,
                version = "14.2.0",
            ),
            identification = PartnerReportIdentificationCreate(
                projectIdentifier = "XE.1_0001",
                projectAcronym = "project acronym",
                partnerNumber = 4,
                partnerAbbreviation = "abbr",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "name in orig",
                nameInEnglish = "name in eng",
                legalStatusId = 697854L,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                country = "Österreich (AT)",
                countryCode = null,
                currency = "EUR",
                coFinancing = coFinancing
            ),
            workPackages = emptyList(),
            targetGroups = emptyList(),
            budget = budget,
        )

        private val workPlan = listOf(
            ProjectWorkPackage(
                id = WORK_PACKAGE_ID,
                workPackageNumber = 2,
                activities = listOf(
                    WorkPackageActivity(
                        id = ACTIVITY_ID,
                        workPackageId = WORK_PACKAGE_ID,
                        activityNumber = 1,
                        title = setOf(InputTranslation(EN, "4.1 activity title")),
                        deliverables = listOf(
                            WorkPackageActivityDeliverable(
                                id = DELIVERABLE_ID,
                                deliverableNumber = 1,
                                title = setOf(InputTranslation(EN, "4.1.1 deliverable title")),
                            ),
                        )
                    )
                ),
                outputs = listOf(
                    WorkPackageOutput(
                        workPackageId = WORK_PACKAGE_ID,
                        outputNumber = 7,
                        title = setOf(InputTranslation(EN, "7 output title")),
                    )
                ),
            )
        )

        private val benefits = listOf(
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.EducationTrainingCentreAndSchool,
                specification = setOf(InputTranslation(language = EN, ProjectTargetGroupDTO.EducationTrainingCentreAndSchool.name))
            ),
        )
    }

    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var projectPartnerPersistence: PartnerPersistence
    @MockK
    lateinit var partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence
    @MockK
    lateinit var projectWorkPackagePersistence: WorkPackagePersistence
    @MockK
    lateinit var projectDescriptionPersistence: ProjectDescriptionPersistence
    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportCreatePersistence: ProjectReportCreatePersistence
    @MockK
    lateinit var currencyPersistence: CurrencyPersistence
    @MockK
    lateinit var createProjectPartnerReportBudget: CreateProjectPartnerReportBudget
    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var createReport: CreateProjectPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "can create report when status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"])
    fun createReportFor(status: ApplicationStatus) {
        val partnerId = 66L
        val detail = partnerDetail(partnerId, "AT")
        every { projectPartnerPersistence.getProjectIdForPartnerId(partnerId) } returns PROJECT_ID
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns "14.2.0"
        every { projectPersistence.getProject(PROJECT_ID, "14.2.0") } returns projectSummary(status)
        every { reportPersistence.countForPartner(partnerId) } returns 24
        every { reportPersistence.getCurrentLatestReportNumberForPartner(partnerId) } returns 7
        every { partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, "14.2.0") } returns
            ProjectPartnerCoFinancingAndContribution(coFinancing, contributions, "")
        every { projectPartnerPersistence.getById(partnerId, "14.2.0") } returns detail
        every { currencyPersistence.getCurrencyForCountry("AT") } returns "EUR"
        // work plan
        every { projectWorkPackagePersistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(PROJECT_ID, "14.2.0") } returns workPlan
        // budget
        val budgetMock = mockk<PartnerReportBudget>()
        every { createProjectPartnerReportBudget.retrieveBudgetDataFor(PROJECT_ID, partnerId, "14.2.0", contributions) } returns budgetMock
        // identification
        every { projectDescriptionPersistence.getBenefits(PROJECT_ID, "14.2.0") } returns benefits

        val slotReport = slot<ProjectPartnerReportCreate>()
        val createdReport = mockk<ProjectPartnerReportSummary>()
        every { createdReport.id } returns 50L
        every { reportCreatePersistence.createPartnerReport(capture(slotReport)) } returns createdReport

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        createReport.createReportFor(partnerId)

        assertThat(slotReport.captured).isEqualTo(expectedCreationObject(partnerId, budgetMock))
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_ADDED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("XE.1_0001")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("project acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(50L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "[XE.1_0001] [PP4] Partner report R.8 added"
        )
    }

    @Test
    fun createReportLimited() {
        val partnerId = 67L
        val detail = partnerDetail(partnerId, null)
        every { projectPartnerPersistence.getProjectIdForPartnerId(partnerId) } returns PROJECT_ID
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns "14.2.0"
        every { projectPersistence.getProject(PROJECT_ID, "14.2.0") } returns projectSummary(ApplicationStatus.CONTRACTED)
        every { reportPersistence.countForPartner(partnerId) } returns 24
        every { reportPersistence.getCurrentLatestReportNumberForPartner(partnerId) } returns 7
        every { partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, "14.2.0") } returns
            ProjectPartnerCoFinancingAndContribution(coFinancing, emptyList(), "")
        every { projectPartnerPersistence.getById(partnerId, "14.2.0") } returns detail
        every { currencyPersistence.getCurrencyForCountry("AT") } returns "EUR"
        // work plan
        every { projectWorkPackagePersistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(PROJECT_ID, "14.2.0") } returns emptyList()
        // budget
        val budgetMock = mockk<PartnerReportBudget>()
        every { createProjectPartnerReportBudget.retrieveBudgetDataFor(PROJECT_ID, partnerId, "14.2.0", emptyList()) } returns budgetMock
        // identification
        every { projectDescriptionPersistence.getBenefits(PROJECT_ID, "14.2.0") } returns null

        val slotReport = slot<ProjectPartnerReportCreate>()
        val createdReport = mockk<ProjectPartnerReportSummary>()
        every { createdReport.id } returns 50L
        every { reportCreatePersistence.createPartnerReport(capture(slotReport)) } returns createdReport

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        createReport.createReportFor(partnerId)

        assertThat(slotReport.captured).isEqualTo(expectedCreationObjectLimited(partnerId, budgetMock))
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_ADDED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("XE.1_0001")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("project acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(50L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "[XE.1_0001] [PP4] Partner report R.8 added"
        )
    }

    @ParameterizedTest(name = "cannot create report when status {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun cannotCreateReportFor(status: ApplicationStatus) {
        val partnerId = 67L
        every { projectPartnerPersistence.getProjectIdForPartnerId(partnerId) } returns PROJECT_ID
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns "6.7.2"
        every { projectPersistence.getProject(PROJECT_ID, "6.7.2") } returns projectSummary(status)
        every { reportPersistence.countForPartner(partnerId) } returns 24

        assertThrows<ReportCanBeCreatedOnlyWhenContractedException> { createReport.createReportFor(partnerId) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `cannotCreateReport max amount reached`() {
        val partnerId = 70L
        every { projectPartnerPersistence.getProjectIdForPartnerId(partnerId) } returns PROJECT_ID
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns "6.7.2"
        every { projectPersistence.getProject(PROJECT_ID, "6.7.2") } returns projectSummary(ApplicationStatus.CONTRACTED)
        every { reportPersistence.countForPartner(partnerId) } returns 25

        assertThrows<MaxAmountOfReportsReachedException> { createReport.createReportFor(partnerId) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
