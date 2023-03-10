package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorRepository
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ReportProjectCertificateCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory.ReportProjectCertificateCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportHorizontalPrincipleRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportResultCreate
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportPersistenceProviderTest : UnitTest() {

    companion object {
        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val WEEK_AGO = LocalDate.now().minusWeeks(1)
        private val YEAR_AGO = LocalDate.now().minusYears(1)

        private fun reportEntity(id: Long, projectId: Long, deadline: ProjectContractingReportingEntity? = null) = ProjectReportEntity(
            id = id,
            projectId = projectId,
            number = 1,
            status = ProjectReportStatus.Draft,
            applicationFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadline = deadline,
            reportingDate = YESTERDAY.minusDays(1),
            periodNumber = 4,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
            verificationDate = null,
        )

        private fun report(id: Long, projectId: Long) = ProjectReportModel(
            id = id,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadlineId = null,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            projectId = projectId,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
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

        private fun partnerReport() = ProjectPartnerReportEntity(
            id = 9L,
            partnerId = 9L,
            number = 9,
            status = ReportStatus.Certified,
            applicationFormVersion = "",
            firstSubmission = null,
            controlEnd = null,
            identification = mockk(),
            createdAt = ZonedDateTime.now(),
            projectReport = null,
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

        fun projectResult(): List<ProjectReportResultCreate> = listOf(mockk {
            every { resultNumber } returns 57
            every { periodNumber } returns 1
            every { programmeResultIndicatorId } returns 2
            every { baseline } returns BigDecimal.valueOf(3)
            every { targetValue } returns BigDecimal.valueOf(4)
            every { previouslyReported } returns BigDecimal.valueOf(5)
            every { deactivated } returns false
        })

        fun projectManagement(): ProjectHorizontalPrinciples = mockk {
            every { sustainableDevelopmentCriteriaEffect } returns mockk()
            every { equalOpportunitiesEffect } returns mockk()
            every { sexualEqualityEffect } returns mockk()
        }
    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    private lateinit var contractingDeadlineRepository: ProjectContractingReportingRepository

    @MockK
    private lateinit var reportIdentificationTargetGroupRepository: ProjectReportIdentificationTargetGroupRepository

    @MockK
    private lateinit var partnerRepository: ProjectPartnerRepository

    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository

    @MockK
    private lateinit var projectReportCoFinancingRepository: ProjectReportCoFinancingRepository

    @MockK
    private lateinit var programmeFundRepository: ProgrammeFundRepository

    @MockK
    private lateinit var projectReportCertificateCoFinancingRepository: ReportProjectCertificateCoFinancingRepository

    @MockK
    private lateinit var projectReportCertificateCostCategoryRepository: ReportProjectCertificateCostCategoryRepository

    @MockK
    private lateinit var resultIndicatorRepository: ResultIndicatorRepository

    @MockK
    private lateinit var projectResultRepository: ProjectReportProjectResultRepository

    @MockK
    private lateinit var horizontalPrincipleRepository: ProjectReportHorizontalPrincipleRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            projectReportRepository,
            contractingDeadlineRepository,
            projectReportSpendingProfileRepository,
            projectReportCoFinancingRepository,
            programmeFundRepository,
            projectReportCertificateCoFinancingRepository,
            projectReportCertificateCostCategoryRepository,
            resultIndicatorRepository,
            projectResultRepository,
            horizontalPrincipleRepository
        )
    }

    @Test
    fun listReports() {
        val projectId = 95L
        val report = reportEntity(42L, projectId)
        every { projectReportRepository.findAllByProjectId(projectId, Pageable.unpaged()) } returns PageImpl(listOf(report))
        assertThat(persistence.listReports(projectId, Pageable.unpaged())).containsExactly(report(42L, projectId))
    }

    @Test
    fun getReportById() {
        val projectId = 94L
        val deadline = ProjectContractingReportingEntity(804L, mockk(), ContractingDeadlineType.Finance, 5, MONTH_AGO, "")
        val report = reportEntity(45L, projectId, deadline)
        every { projectReportRepository.getByIdAndProjectId(22L, projectId) } returns report
        assertThat(persistence.getReportById(projectId, 22L)).isEqualTo(
            report(45L, projectId).copy(
                deadlineId = 804L,
                type = ContractingDeadlineType.Finance,
                periodNumber = 5,
                reportingDate = MONTH_AGO,
            )
        )
    }

    @Test
    fun createReport() {
        val projectId = 93L

        val deadline = mockk<ProjectContractingReportingEntity>()
        every { deadline.id } returns 54L
        every { contractingDeadlineRepository.findTop50ByProjectIdOrderByDeadline(projectId) } returns mutableListOf(deadline)

        val targetGroupsSlot = slot<Iterable<ProjectReportIdentificationTargetGroupEntity>>()

        val saveSlot = slot<ProjectReportEntity>()
        every { projectReportRepository.save(capture(saveSlot)) } returnsArgument 0
        every { reportIdentificationTargetGroupRepository.saveAll(capture(targetGroupsSlot)) } returnsArgument 0

        val partner = mockk<ProjectPartnerEntity>()
        every { partner.id } returns 8789L
        every { partnerRepository.findTop30ByProjectId(projectId) } returns listOf(partner)
        val partnerReport = partnerReport()
        every { partnerReportRepository.findAllByPartnerIdInAndProjectReportNullAndStatus(setOf(8789L), ReportStatus.Certified) } returns
            listOf(partnerReport)
        every { projectReportSpendingProfileRepository.saveAll(listOf()) } returnsArgument 0
        every { projectReportCoFinancingRepository.saveAll(listOf()) } returnsArgument 0
        every { projectReportCertificateCoFinancingRepository.save(any()) } returnsArgument 0
        every { projectReportCertificateCostCategoryRepository.save(any()) } returnsArgument 0
        every { projectReportRepository.getSubmittedProjectReportIds(projectId) } returns setOf(567L)
        every { projectResultRepository.getCumulativeValues(setOf(567L)) } returns listOf(Pair(57, BigDecimal.TEN))
        every { projectResultRepository.saveAll(any<List<ProjectReportProjectResultEntity>>()) } returnsArgument 0
        every { horizontalPrincipleRepository.save(any()) } returnsArgument 0
        every { resultIndicatorRepository.getById(2) } returns mockk()

        assertThat(
            persistence.createReportAndFillItToEmptyCertificates(
                ProjectReportCreateModel(
                    report(0L, projectId).copy(periodNumber = null),
                    budget,
                    projectRelevanceBenefits(),
                    mapOf(),
                    projectResult(),
                    projectManagement(),
                )
            )
        )
            .isEqualTo(report(0L /* is changed by DB */, projectId).copy(periodNumber = null))
        assertThat(saveSlot.captured.projectId).isEqualTo(projectId)

        assertThat(partnerReport.projectReport).isEqualTo(saveSlot.captured)
    }

    @Test
    fun updateReport() {
        val projectId = 92L
        val report = reportEntity(14L, projectId, deadline = null)
        every { projectReportRepository.getByIdAndProjectId(14L, projectId) } returns report

        val deadlineEntity = ProjectContractingReportingEntity(84L, mockk(), ContractingDeadlineType.Finance, 5, MONTH_AGO, "")
        every { contractingDeadlineRepository.findByProjectIdAndId(projectId, 84L) } returns deadlineEntity

        val deadline = ProjectReportDeadline(
            deadlineId = 84L,
            type = ContractingDeadlineType.Finance,
            periodNumber = 14,
            reportingDate = WEEK_AGO,
        )
        assertThat(
            persistence.updateReport(
                projectId,
                reportId = 14L, startDate = WEEK_AGO, endDate = YEAR_AGO, deadline = deadline
            )
        ).isEqualTo(
            report(14L, projectId = projectId).copy(
                // taken from deadline entity, because deadlineId is not null
                startDate = WEEK_AGO,
                endDate = YEAR_AGO,
                deadlineId = 84L,
                type = ContractingDeadlineType.Finance,
                periodNumber = 5,
                reportingDate = MONTH_AGO,
            )
        )

        // asserting report entity itself
        assertThat(report.startDate).isEqualTo(WEEK_AGO)
        assertThat(report.endDate).isEqualTo(YEAR_AGO)
        assertThat(report.deadline?.id).isEqualTo(84L)
        assertThat(report.type).isEqualTo(ContractingDeadlineType.Finance)
        assertThat(report.periodNumber).isEqualTo(14)
        assertThat(report.reportingDate).isEqualTo(WEEK_AGO)
    }

    @Test
    fun deleteReport() {
        every { projectReportRepository.deleteByProjectIdAndId(any(), any()) } answers { }
        persistence.deleteReport(92L, 47L)
        verify(exactly = 1) { projectReportRepository.deleteByProjectIdAndId(92L, 47L) }
    }

    @Test
    fun getCurrentLatestReportFor() {
        val projectId = 90L
        every { projectReportRepository.findFirstByProjectIdOrderByIdDesc(projectId) } returns reportEntity(48L, projectId)
        assertThat(persistence.getCurrentLatestReportFor(projectId)).isEqualTo(report(48L, projectId))
    }

    @Test
    fun countForProject() {
        val projectId = 89L
        every { projectReportRepository.countAllByProjectId(projectId) } returns 15
        assertThat(persistence.countForProject(projectId)).isEqualTo(15)
    }

    @Test
    fun getCurrentSpendingProfile() {
        val projectReportId = 1L
        every { partnerReportRepository.findTotalAfterControlPerPartner(projectReportId) } returns
            listOf(Pair(10L, BigDecimal(400)), Pair(11L, BigDecimal(200)))
        assertThat(persistence.getCurrentSpendingProfile(projectReportId)).isEqualTo(
            mapOf(10L to BigDecimal(400), 11L to BigDecimal(200))
        )
    }

    @Test
    fun getSubmittedProjectReportIds() {
        val projectId = 1L
        every { projectReportRepository.getSubmittedProjectReportIds(projectId) } returns
            setOf(10L, 11L)
        assertThat(persistence.getSubmittedProjectReportIds(projectId)).isEqualTo(setOf(10L, 11L))
    }
}
