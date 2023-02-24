package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.toScaledBigDecimal
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

        private fun leadPartnerSummary(): ProjectPartnerSummary {
            val mock = mockk<ProjectPartnerSummary>()
            every { mock.role } returns ProjectPartnerRole.LEAD_PARTNER
            every { mock.id } returns 1L
            return mock
        }

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

        private val call = CallDetail(
            id = 1,
            name = "call",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now(),
            endDateStep1 = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = null,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )

        private val projectPartnerCoFinancingAndContribution = ProjectPartnerCoFinancingAndContribution(
            finances = emptyList(),
            partnerContributions = emptyList(),
            partnerAbbreviation = ""
        )

        private val totalCostPartner = 80.toScaledBigDecimal()

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
            previouslyReportedCoFinancing = PreviouslyProjectReportedCoFinancing(
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
    private lateinit var callPersistence: CallPersistence
    @MockK
    private lateinit var projectBudgetPersistence: ProjectBudgetPersistence
    @MockK
    private lateinit var projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistenceProvider
    @MockK
    private lateinit var getBudgetTotalCost: GetBudgetTotalCost
    @MockK
    private lateinit var partnerBudgetPerFundCalculator: PartnerBudgetPerFundCalculatorService

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
            callPersistence,
            projectBudgetPersistence,
            projectPartnerCoFinancingPersistence,
            getBudgetTotalCost,
            partnerBudgetPerFundCalculator
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
        every { reportPersistence.getSubmittedProjectReportIds(projectId) } returns setOf()
        every { projectReportIdentificationPersistence.getSpendingProfileCumulative(any()) } returns mapOf()
        every { projectReportIdentificationPersistence.getSpendingProfileCumulative(any()) } returns mapOf()
        every { projectBudgetPersistence.getPartnersForProjectId(projectId, "version") } returns listOf(leadPartnerSummary())
        every { callPersistence.getCallByProjectId(projectId) } returns call
        every { projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(1L, "version") } returns projectPartnerCoFinancingAndContribution
        every { getBudgetTotalCost.getBudgetTotalCost(1L, "version") } returns totalCostPartner
        every { partnerBudgetPerFundCalculator.calculate(any(), any(), any(), any())} returns listOf(result)
        every { createProjectReportBudget.retrieveBudgetDataFor(any(), any(), any())} returns budget

        val reportStored = slot<ProjectReportModel>()
        every { reportPersistence.createReportAndFillItToEmptyCertificates(
            capture(reportStored),
            projectRelevanceBenefits(),
            mapOf(1L to BigDecimal.ZERO),
            budget
        )} returnsArgument 0

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
        assertThat(created).isEqualTo(expectedProjectReport(projectId).copy(createdAt = reportStored.captured.createdAt))

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
