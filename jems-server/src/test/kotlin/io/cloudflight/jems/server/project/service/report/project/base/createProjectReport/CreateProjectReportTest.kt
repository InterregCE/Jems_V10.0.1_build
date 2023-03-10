package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class CreateProjectReportTest : UnitTest() {

    companion object {
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private fun project(id: Long, status: ApplicationStatus): ProjectFull {
            val statusMock = mockk<ProjectStatus>()
            every { statusMock.status } returns status
            val mock = mockk<ProjectFull>()

            every { mock.id } returns id
            every { mock.acronym } returns "proj-acr"
            every { mock.customIdentifier } returns "proj-custom-iden"
            every { mock.projectStatus } returns statusMock

            return mock
        }

        private fun currentLatestReport(number: Int): ProjectReportModel {
            val mock = mockk<ProjectReportModel>()
            every { mock.reportNumber } returns number
            return mock
        }

        private fun leadPartner(): ProjectPartnerDetail {
            val mock = mockk<ProjectPartnerDetail>()
            every { mock.role } returns ProjectPartnerRole.LEAD_PARTNER
            every { mock.nameInEnglish } returns "lead-en"
            every { mock.nameInOriginalLanguage } returns "lead-orig"
            every { mock.id } returns 1L
            return mock
        }

        private fun projectResult(): ProjectResult = mockk {
            every { resultNumber } returns 1
            every { periodNumber } returns 2
            every { programmeResultIndicatorId } returns null
            every { baseline } returns BigDecimal.valueOf(3)
            every { targetValue } returns BigDecimal.valueOf(4)
            every { deactivated } returns false
        }

        private fun projectManagement(): ProjectManagement = mockk {
            every { projectHorizontalPrinciples } returns null
        }

        private fun projectReportModel(projectId: Long) = ProjectReportModel(
            id = 0L,
            reportNumber = 8,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "version",
            startDate = YESTERDAY,
            endDate = TOMORROW,

            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),

            projectId = projectId,
            projectIdentifier = "proj-custom-iden",
            projectAcronym = "proj-acr",
            leadPartnerNameInOriginalLanguage = "lead-orig",
            leadPartnerNameInEnglish = "lead-en",

            createdAt = ZonedDateTime.now(),
            firstSubmission = null,
            verificationDate = null,
        )

        private fun expectedProjectReport(projectId: Long) = ProjectReport(
            id = 0L,
            reportNumber = 8,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "version",
            startDate = YESTERDAY,
            endDate = TOMORROW,

            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodDetail = ProjectPeriod(4, 17, 22),
            reportingDate = YESTERDAY.minusDays(1),

            projectId = projectId,
            projectIdentifier = "proj-custom-iden",
            projectAcronym = "proj-acr",
            leadPartnerNameInOriginalLanguage = "lead-orig",
            leadPartnerNameInEnglish = "lead-en",

            createdAt = ZonedDateTime.now(),
            firstSubmission = null,
            verificationDate = null,
        )

        private fun projectRelevanceBenefits() = listOf(
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.Hospitals,
                specification = setOf(
                    InputTranslation(SystemLanguage.EN, "en"),
                    InputTranslation(SystemLanguage.DE, "de")
                )
            ),
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.CrossBorderLegalBody,
                specification = setOf(
                    InputTranslation(SystemLanguage.EN, "en 2"),
                    InputTranslation(SystemLanguage.DE, "de 2")
                )
            )
        )

        val result = ProjectPartnerBudgetPerFund(
            partner = null,
            budgetPerFund = emptySet(),
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal.ZERO,
            totalEligibleBudget = BigDecimal.ZERO,
            percentageOfTotalEligibleBudget = BigDecimal.ZERO
        )

        val budget = ProjectReportBudget(
            coFinancing = PreviouslyProjectReportedCoFinancing(
                fundsSorted = emptyList(),
                totalPartner = BigDecimal.ZERO,
                totalPublic = BigDecimal.ZERO,
                totalAutoPublic = BigDecimal.ZERO,
                totalPrivate = BigDecimal.ZERO,
                totalSum = BigDecimal.ZERO,
                previouslyReportedSum = BigDecimal.ZERO,
                previouslyReportedPrivate = BigDecimal.ZERO,
                previouslyReportedAutoPublic = BigDecimal.ZERO,
                previouslyReportedPublic = BigDecimal.ZERO,
                previouslyReportedPartner = BigDecimal.ZERO,
            ),
            costCategorySetup = ReportCertificateCostCategory(
                totalsFromAF = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.ZERO,
                    travel = BigDecimal.ZERO,
                    office = BigDecimal.ZERO,
                    external = BigDecimal.ZERO,
                    equipment = BigDecimal.ZERO,
                    infrastructure = BigDecimal.ZERO,
                    other = BigDecimal.ZERO,
                    lumpSum = BigDecimal.ZERO,
                    unitCost = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO
                ),
                currentlyReported = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.ZERO,
                    travel = BigDecimal.ZERO,
                    office = BigDecimal.ZERO,
                    external = BigDecimal.ZERO,
                    equipment = BigDecimal.ZERO,
                    infrastructure = BigDecimal.ZERO,
                    other = BigDecimal.ZERO,
                    lumpSum = BigDecimal.ZERO,
                    unitCost = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO
                ),
                previouslyReported = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.ZERO,
                    travel = BigDecimal.ZERO,
                    office = BigDecimal.ZERO,
                    external = BigDecimal.ZERO,
                    equipment = BigDecimal.ZERO,
                    infrastructure = BigDecimal.ZERO,
                    other = BigDecimal.ZERO,
                    lumpSum = BigDecimal.ZERO,
                    unitCost = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO
                )
            )
        )
    }

    @MockK
    private lateinit var versionPersistence: ProjectVersionPersistence
    @MockK
    private lateinit var projectPersistence: ProjectPersistence
    @MockK
    private lateinit var projectPartnerPersistence: PartnerPersistence
    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher
    @MockK
    private lateinit var projectDescriptionPersistence: ProjectDescriptionPersistence
    @MockK
    private lateinit var projectReportIdentificationPersistence: ProjectReportIdentificationPersistence
    @MockK
    private lateinit var createProjectReportBudget: CreateProjectReportBudget
    @MockK
    private lateinit var projectResultPersistence: ProjectResultPersistence
    @MockK
    private lateinit var projectReportResultPersistence: ProjectReportResultPrinciplePersistence

    @InjectMockKs
    lateinit var interactor: CreateProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(
            versionPersistence,
            projectPersistence,
            projectPartnerPersistence,
            reportPersistence,
            auditPublisher,
            projectReportIdentificationPersistence,
            createProjectReportBudget,
            projectResultPersistence,
            projectReportResultPersistence
        )
    }

    @ParameterizedTest(name = "createReportFor {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"])
    fun createReportFor(status: ApplicationStatus) {
        val projectId = 54L + status.ordinal
        every { reportPersistence.countForProject(projectId) } returns 1
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "version"
        every { projectPersistence.getProject(projectId, "version") } returns project(projectId, status)
        every { projectPersistence.getProjectPeriods(projectId, "version") } returns listOf(ProjectPeriod(4, 17, 22))
        every { reportPersistence.getCurrentLatestReportFor(projectId) } returns currentLatestReport(7)
        every { projectPartnerPersistence.findTop30ByProjectId(projectId, "version") } returns listOf(leadPartner())
        every { projectDescriptionPersistence.getBenefits(projectId, "version") } returns projectRelevanceBenefits()
        every { reportPersistence.getSubmittedProjectReportIds(projectId) } returns setOf(11L)
        every { projectReportIdentificationPersistence.getSpendingProfileCumulative(any()) } returns mapOf()
        every { projectReportIdentificationPersistence.getSpendingProfileCumulative(any()) } returns mapOf()
        every { createProjectReportBudget.retrieveBudgetDataFor(any(), any())} returns budget
        every { projectResultPersistence.getResultsForProject(projectId, "version") } returns listOf(projectResult())
        every { projectDescriptionPersistence.getProjectManagement(projectId, "version") } returns projectManagement()
        every { projectReportResultPersistence.getResultCumulative(any()) } returns mapOf()

        val reportStored = slot<ProjectReportCreateModel>()
        every { reportPersistence.createReportAndFillItToEmptyCertificates(capture(reportStored))} returns projectReportModel(projectId)

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}

        val data = ProjectReportUpdate(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
        )
        val created = interactor.createReportFor(projectId, data)
        assertThat(created).isEqualTo(
            expectedProjectReport(projectId).copy(createdAt = created.createdAt)
        )

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_REPORT_ADDED,
            project = AuditProject(projectId.toString(), "proj-custom-iden", "proj-acr"),
            entityRelatedId = 0L,
            description = "[proj-custom-iden] Project report PR.8 added",
        ))
    }

    @ParameterizedTest(name = "createReportFor - not contracted {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun `createReportFor - not contracted`(status: ApplicationStatus) {
        val projectId = 154L + status.ordinal
        every { reportPersistence.countForProject(projectId) } returns 1
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "version"
        every { projectPersistence.getProject(projectId, "version") } returns project(projectId, status)

        assertThrows<ReportCanBeCreatedOnlyWhenContractedException> { interactor.createReportFor(projectId, mockk()) }
    }

    @Test
    fun `createReportFor - max amounts of reports reached`() {
        val projectId = 254L
        every { reportPersistence.countForProject(projectId) } returns 25
        assertThrows<MaxAmountOfReportsReachedException> { interactor.createReportFor(projectId, mockk()) }
    }

}
