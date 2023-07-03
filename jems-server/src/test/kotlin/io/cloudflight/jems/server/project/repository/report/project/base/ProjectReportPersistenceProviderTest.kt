package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

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
            verificationEndDate = null,
            amountRequested = BigDecimal.ZERO,
            totalEligibleAfterVerification = BigDecimal.ZERO
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
            verificationEndDate = null,
            amountRequested = BigDecimal.ZERO,
            totalEligibleAfterVerification = BigDecimal.ZERO
        )

        fun report(id: Long, deadlineType: ContractingDeadlineType?, reportType: ContractingDeadlineType?): ProjectReportEntity {
            val report = mockk<ProjectReportEntity>()
            every { report.id } returns id
            if (deadlineType == null) {
                every { report.deadline } returns null
            } else {
                val deadline = mockk<ProjectContractingReportingEntity>()
                every { deadline.type } returns deadlineType
                every { report.deadline } returns deadline
            }
            every { report.type } returns reportType
            return report
        }

        private fun draftReportSubmissionEntity(id: Long, projectId: Long) = ProjectReportSubmissionSummary(
            id = id,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            version = "3.0",
            firstSubmission = LAST_YEAR,
            createdAt = LAST_WEEK,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            projectId = projectId
        )
    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    private lateinit var contractingDeadlineRepository: ProjectContractingReportingRepository

    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            projectReportRepository,
            contractingDeadlineRepository,
            partnerReportRepository,
            projectReportSpendingProfileRepository,
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
    fun getAllProjectReportsBaseDataByProjectId() {
        val streamData = Stream.of(
            ProjectReportBaseData(80L, "v1.0", 1),
            ProjectReportBaseData(81L, "v1.0", 2),
            ProjectReportBaseData(82L, "v1.0", 1),
        )
        val sequence = sequenceOf(
            ProjectReportBaseData(80L, "v1.0", 1),
            ProjectReportBaseData(81L, "v1.0", 2),
            ProjectReportBaseData(82L, "v1.0", 1),
        )

        every { projectReportRepository.findAllProjectReportsBaseDataByProjectId(75L) } returns streamData
        assertThat(persistence.getAllProjectReportsBaseDataByProjectId(75L).toList())
            .isEqualTo(sequence.toList())
    }

    @Test
    fun getReportById() {
        val projectId = 94L
        val deadline = ProjectContractingReportingEntity(804L, mockk(), ContractingDeadlineType.Finance, 5, MONTH_AGO, "", number = 1)
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
    fun updateReport() {
        val projectId = 92L
        val report = reportEntity(14L, projectId, deadline = null)
        every { projectReportRepository.getByIdAndProjectId(14L, projectId) } returns report

        val deadlineEntity = ProjectContractingReportingEntity(84L, mockk(), ContractingDeadlineType.Finance, 5, MONTH_AGO, "", number = 1)
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
        every {
            projectReportRepository.findAllByProjectIdAndStatusInOrderByNumberDesc(
                projectId, setOf(
                    ProjectReportStatus.Submitted,
                    ProjectReportStatus.InVerification
                )
            )
        } returns listOf(
            report(id = 45L, deadlineType = null, reportType = ContractingDeadlineType.Content),
            report(id = 46L, deadlineType = ContractingDeadlineType.Both, reportType = ContractingDeadlineType.Content),
            report(id = 47L, deadlineType = ContractingDeadlineType.Finance, reportType = null),
        )
        assertThat(persistence.getSubmittedProjectReportIds(projectId)).containsExactly(
            Pair(45L, ContractingDeadlineType.Content),
            Pair(46L, ContractingDeadlineType.Both),
            Pair(47L, ContractingDeadlineType.Finance),
        )
    }

    @Test
    fun updateNewerReportNumbersIfAllOpen() {
        val projectId = 3L
        val reportNumber = 4
        val reports = listOf(
            reportEntity(5, projectId).apply { number = 5 },
            reportEntity(6, projectId).apply { number = 6 },
            reportEntity(7, projectId).apply { number = 7 },
        )
        every { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) } returns reports

        persistence.decreaseNewerReportNumbersIfAllOpen(projectId, reportNumber)

        verify(exactly = 1) { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) }
        assertThat(reports.map { it.number }).containsExactly(4, 5, 6)
    }

    @Test
    fun doNotUpdateNewerReportNumbersIfAnyClosed() {
        val projectId = 3L
        val reportNumber = 4
        val reports = listOf(
            reportEntity(5, projectId).apply { number = 5 },
            reportEntity(6, projectId).apply { number = 6 }.apply { status = ProjectReportStatus.Submitted },
            reportEntity(7, projectId).apply { number = 7 },
        )
        every { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) } returns reports

        persistence.decreaseNewerReportNumbersIfAllOpen(projectId, reportNumber)

        verify(exactly = 1) { projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, reportNumber) }
        assertThat(reports.map { it.number }).containsExactly(5, 6, 7)
    }

    @Test
    fun startVerificationOnReportById() {
        val projectId = 3L

        val report = reportEntity(id = 47L, projectId)
        every { projectReportRepository.getByIdAndProjectId(47L, projectId) } returns report

        val inVerificationReport = persistence.startVerificationOnReportById(projectId, 47L)

        assertThat(inVerificationReport).isEqualTo(
            draftReportSubmissionEntity(id = 47L, projectId).copy(
                status = ProjectReportStatus.InVerification,
            )
        )
    }

}
