package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
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

    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository
    @MockK
    private lateinit var contractingDeadlineRepository: ProjectContractingReportingRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(projectReportRepository, contractingDeadlineRepository)
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

        val saveSlot = slot<ProjectReportEntity>()
        every { projectReportRepository.save(capture(saveSlot)) } returnsArgument 0

        val reportToCreate = report(0L, projectId).copy(periodNumber = null)
        assertThat(persistence.createReport(reportToCreate))
            .isEqualTo(report(0L /* is changed by DB */, projectId).copy(periodNumber = null))
        assertThat(saveSlot.captured.projectId).isEqualTo(projectId)
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
        assertThat(persistence.updateReport(projectId,
            reportId = 14L, startDate = WEEK_AGO, endDate = YEAR_AGO, deadline = deadline
        )).isEqualTo(report(14L, projectId = projectId).copy(
            // taken from deadline entity, because deadlineId is not null
            startDate = WEEK_AGO,
            endDate = YEAR_AGO,
            deadlineId = 84L,
            type = ContractingDeadlineType.Finance,
            periodNumber = 5,
            reportingDate = MONTH_AGO,
        ))

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

}
