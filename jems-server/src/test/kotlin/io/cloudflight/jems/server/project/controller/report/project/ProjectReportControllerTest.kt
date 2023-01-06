package io.cloudflight.jems.server.project.controller.report.project

import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.contracting.reporting.ContractingDeadlineTypeDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.project.base.createProjectReport.CreateProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport.DeleteProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList.GetProjectReportListInteractor
import io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport.SubmitProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport.UpdateProjectReportInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ProjectReportControllerTest : UnitTest() {

    private val YESTERDAY = ZonedDateTime.now().minusDays(1)
    private val WEEK_AGO = LocalDate.now().minusWeeks(1)
    private val TOMORROW = LocalDate.now().plusDays(1)
    private val MONTH_AGO = ZonedDateTime.now().minusMonths(1)
    private val YEAR_AGO = ZonedDateTime.now().minusYears(1)

    private val report = ProjectReport(
        id = 52L,
        reportNumber = 6,
        status = ProjectReportStatus.Draft,
        linkedFormVersion = "4.0",
        startDate = WEEK_AGO,
        endDate = TOMORROW,
        deadlineId = 560L,
        type = ContractingDeadlineType.Both,
        periodDetail = ProjectPeriod(2, 4, 6),
        reportingDate = null,
        projectId = 25L,
        projectIdentifier = "iden",
        projectAcronym = "acr",
        leadPartnerNameInOriginalLanguage = "name orig",
        leadPartnerNameInEnglish = "name EN",
        createdAt = YEAR_AGO,
        firstSubmission = MONTH_AGO,
        verificationDate = YESTERDAY,
    )

    private val expectedReport = ProjectReportDTO(
        id = 52L,
        reportNumber = 6,
        status = ProjectReportStatusDTO.Draft,
        linkedFormVersion = "4.0",
        startDate = WEEK_AGO,
        endDate = TOMORROW,
        deadlineId = 560L,
        type = ContractingDeadlineTypeDTO.Both,
        periodDetail = ProjectPeriodDTO(0L, 2, 4, 6),
        reportingDate = null,
        projectId = 25L,
        projectIdentifier = "iden",
        projectAcronym = "acr",
        leadPartnerNameInOriginalLanguage = "name orig",
        leadPartnerNameInEnglish = "name EN",
        createdAt = YEAR_AGO,
        firstSubmission = MONTH_AGO,
        verificationDate = YESTERDAY,
    )

    private val reportSummary = ProjectReportSummary(
        id = 52L,
        reportNumber = 6,
        status = ProjectReportStatus.Draft,
        linkedFormVersion = "4.0",
        type = ContractingDeadlineType.Both,
        periodDetail = ProjectPeriod(2, 4, 6),
        startDate = WEEK_AGO,
        endDate = TOMORROW,
        reportingDate = null,
        createdAt = YEAR_AGO,
        firstSubmission = MONTH_AGO,
        verificationDate = YESTERDAY,
        deletable = false,
    )

    private val expectedReportSummary = ProjectReportSummaryDTO(
        id = 52L,
        reportNumber = 6,
        status = ProjectReportStatusDTO.Draft,
        linkedFormVersion = "4.0",
        type = ContractingDeadlineTypeDTO.Both,
        periodDetail = ProjectPeriodDTO(0L, 2, 4, 6),
        startDate = WEEK_AGO,
        endDate = TOMORROW,
        reportingDate = null,
        createdAt = YEAR_AGO,
        firstSubmission = MONTH_AGO,
        verificationDate = YESTERDAY,
        deletable = false,
    )

    @MockK
    private lateinit var getReportList: GetProjectReportListInteractor
    @MockK
    private lateinit var getReport: GetProjectReportInteractor
    @MockK
    private lateinit var createReport: CreateProjectReportInteractor
    @MockK
    private lateinit var updateReport: UpdateProjectReportInteractor
    @MockK
    private lateinit var deleteReport: DeleteProjectReportInteractor
    @MockK
    private lateinit var submitReport: SubmitProjectReportInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportController

    @BeforeEach
    fun resetMocks() {
        clearMocks(deleteReport)
    }

    @Test
    fun getProjectReportList() {
        every { getReportList.findAll(15L, Pageable.unpaged()) } returns PageImpl(listOf(reportSummary))
        assertThat(controller.getProjectReportList(15L, Pageable.unpaged()).content)
            .containsExactly(expectedReportSummary)
    }

    @Test
    fun getProjectReport() {
        every { getReport.findById(17L, reportId = 8L) } returns report
        assertThat(controller.getProjectReport(17L, reportId = 8L))
            .isEqualTo(expectedReport)
    }

    @Test
    fun createProjectReport() {
        val createSlot = slot<ProjectReportUpdate>()
        every { createReport.createReportFor(19L, capture(createSlot)) } returns report

        val toCreate = ProjectReportUpdateDTO(
            startDate = WEEK_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineTypeDTO.Content,
            periodNumber = null,
            reportingDate = WEEK_AGO,
        )
        assertThat(controller.createProjectReport(19L, toCreate)).isEqualTo(expectedReport)
        assertThat(createSlot.captured).isEqualTo(ProjectReportUpdate(
            startDate = WEEK_AGO,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Content,
            periodNumber = null,
            reportingDate = WEEK_AGO,
        ))
    }

    @Test
    fun updateProjectReport() {
        val updateSlot = slot<ProjectReportUpdate>()
        every { updateReport.updateReport(20L, reportId = 9L, capture(updateSlot)) } returns report

        val toUpdate = ProjectReportUpdateDTO(
            startDate = TOMORROW,
            endDate = WEEK_AGO,
            deadlineId = 4L,
            type = ContractingDeadlineTypeDTO.Both,
            periodNumber = 12,
            reportingDate = TOMORROW,
        )
        assertThat(controller.updateProjectReport(20L, reportId = 9L, toUpdate)).isEqualTo(expectedReport)
        assertThat(updateSlot.captured).isEqualTo(ProjectReportUpdate(
            startDate = TOMORROW,
            endDate = WEEK_AGO,
            deadlineId = 4L,
            type = ContractingDeadlineType.Both,
            periodNumber = 12,
            reportingDate = TOMORROW,
        ))
    }

    @Test
    fun deleteProjectReport() {
        every { deleteReport.delete(21L, reportId = 10L) } answers { }
        controller.deleteProjectReport(21L, reportId = 10L)
        verify(exactly = 1) { deleteReport.delete(21L, reportId = 10L) }
    }

    @Test
    fun submitProjectReport() {
        every { submitReport.submit(21L, reportId = 10L) } returns ProjectReportStatus.Submitted
        controller.submitProjectReport(21L, reportId = 10L)
       assertThat(submitReport.submit(21L, reportId = 10L)).isEqualTo(ProjectReportStatus.Submitted)
    }
}
