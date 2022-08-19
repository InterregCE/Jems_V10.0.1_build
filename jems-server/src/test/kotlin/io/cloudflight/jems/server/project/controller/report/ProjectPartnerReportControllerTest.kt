package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusTypeDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileSearchRequestDTO
import io.cloudflight.jems.api.project.dto.report.file.UserSimpleDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationCoFinancingDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.workflow.createProjectPartnerReport.CreateProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile.DeleteProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile.DownloadProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.ListProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.uploadFileToProjectPartnerReport.UploadFileToProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport.GetProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.workflow.startControlPartnerReport.StartControlPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.workflow.submitProjectPartnerReport.SubmitProjectPartnerReportInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ProjectPartnerReportControllerTest : UnitTest() {

    companion object {
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val LAST_WEEK = LocalDate.now().minusWeeks(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)

        private val reportSummary = ProjectPartnerReportSummary(
            id = 754,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "6.1",
            firstSubmission = null,
            createdAt = YESTERDAY,
            startDate = LAST_WEEK,
            endDate = NEXT_WEEK,
            periodDetail = ProjectPartnerReportPeriod(
                number = 2,
                periodBudget = BigDecimal.ONE,
                periodBudgetCumulative = BigDecimal.TEN,
                start = 4,
                end = 6,
            )
        )

        private val reportSummaryDTO = ProjectPartnerReportSummaryDTO(
            id = reportSummary.id,
            reportNumber = reportSummary.reportNumber,
            status = ReportStatusDTO.Draft,
            linkedFormVersion = reportSummary.version,
            firstSubmission = null,
            createdAt = reportSummary.createdAt,
            startDate = reportSummary.startDate,
            endDate = reportSummary.endDate,
            periodDetail = ProjectPartnerReportPeriodDTO(
                number = 2,
                periodBudget = BigDecimal.ONE,
                periodBudgetCumulative = BigDecimal.TEN,
                start = 4,
                end = 6,
            )
        )

        private val report = ProjectPartnerReport(
            id = reportSummary.id,
            reportNumber = reportSummary.reportNumber,
            status = reportSummary.status,
            version = reportSummary.version,
            firstSubmission = YESTERDAY,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatus = ProgrammeLegalStatus(
                    id = 7L,
                    type = ProgrammeLegalStatusType.PUBLIC,
                    description = setOf(InputTranslation(SystemLanguage.EN, "en desc"))
                ),
                partnerType = ProjectTargetGroup.Egtc,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                country = "Österreich (AT)",
                currency = "EUR",
                coFinancing = listOf(
                    ProjectPartnerCoFinancing(
                        fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                        percentage = BigDecimal.ONE,
                        fund = ProgrammeFund(
                            id = 766L,
                            selected = true,
                            type = ProgrammeFundType.ERDF,
                        )
                    ),
                ),
            ),
        )

        private val reportDTO = ProjectPartnerReportDTO(
            id = reportSummary.id,
            reportNumber = reportSummary.reportNumber,
            status = ReportStatusDTO.Draft,
            linkedFormVersion = reportSummary.version,
            identification = PartnerReportIdentificationDTO(
                projectIdentifier = report.identification.projectIdentifier,
                projectAcronym = report.identification.projectAcronym,
                partnerNumber = report.identification.partnerNumber,
                partnerAbbreviation = report.identification.partnerAbbreviation,
                partnerRole = ProjectPartnerRoleDTO.PARTNER,
                nameInOriginalLanguage = report.identification.nameInOriginalLanguage,
                nameInEnglish = report.identification.nameInEnglish,
                legalStatus = ProgrammeLegalStatusDTO(
                    id = 7L,
                    type = ProgrammeLegalStatusTypeDTO.PUBLIC,
                    description = setOf(InputTranslation(SystemLanguage.EN, "en desc"))
                ),
                partnerType = ProjectTargetGroupDTO.Egtc,
                vatRecovery = ProjectPartnerVatRecoveryDTO.Yes,
                country = "Österreich (AT)",
                currency = "EUR",
                coFinancing = listOf(
                    PartnerReportIdentificationCoFinancingDTO(
                        fund = ProgrammeFundDTO(
                            id = 766L,
                            selected = true,
                            type = ProgrammeFundTypeDTO.ERDF,
                        ),
                        percentage = BigDecimal.ONE,
                    )
                )
            )
        )

        private val reportFile = ProjectReportFile(
            id = 478L,
            name = "attachment.pdf",
            type = ProjectPartnerReportFileType.Contribution,
            uploaded = YESTERDAY,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
        )

        private val reportFileDto = ProjectReportFileDTO(
            id = 478L,
            name = "attachment.pdf",
            type = ProjectPartnerReportFileTypeDTO.Contribution,
            uploaded = YESTERDAY,
            author = UserSimpleDTO(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            sizeString = "46.8\u0020kB",
        )
    }

    @MockK
    lateinit var createPartnerReport: CreateProjectPartnerReportInteractor

    @MockK
    lateinit var submitPartnerReport: SubmitProjectPartnerReportInteractor

    @MockK
    lateinit var startControlReport: StartControlPartnerReportInteractor

    @MockK
    lateinit var getPartnerReport: GetProjectPartnerReportInteractor

    @MockK
    lateinit var downloadPartnerReportFile: DownloadProjectPartnerReportFileInteractor

    @MockK
    lateinit var deletePartnerReportFile: DeleteProjectPartnerReportFileInteractor

    @MockK
    lateinit var listPartnerReportFile: ListProjectPartnerReportFileInteractor

    @MockK
    lateinit var uploadPartnerReportFile: UploadFileToProjectPartnerReportInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportController

    @Test
    fun getProjectPartnerReports() {
        every { getPartnerReport.findAll(14, Pageable.unpaged()) } returns PageImpl(listOf(reportSummary))
        assertThat(
            controller.getProjectPartnerReports(
                14,
                Pageable.unpaged()
            ).content
        ).containsExactly(reportSummaryDTO)
    }

    @Test
    fun getProjectPartnerReport() {
        every { getPartnerReport.findById(14, 240) } returns report
        assertThat(controller.getProjectPartnerReport(14, 240)).isEqualTo(reportDTO)
    }

    @Test
    fun `getProjectPartnerReport - with optional values`() {
        every { getPartnerReport.findById(14, 240) } returns report.copy(
            identification = report.identification.copy(
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
            )
        )
        assertThat(controller.getProjectPartnerReport(14, 240)).isEqualTo(
            reportDTO.copy(
                identification = reportDTO.identification.copy(
                    legalStatus = null,
                    partnerType = null,
                    vatRecovery = null,
                )
            )
        )
    }

    @Test
    fun createProjectPartnerReport() {
        every { createPartnerReport.createReportFor(18) } returns reportSummary
        assertThat(controller.createProjectPartnerReport(18)).isEqualTo(reportSummaryDTO)
    }

    @Test
    fun submitProjectPartnerReport() {
        every { submitPartnerReport.submit(18, 310) } returns ReportStatus.Submitted
        assertThat(controller.submitProjectPartnerReport(18, 310)).isEqualTo(ReportStatusDTO.Submitted)
    }

    @Test
    fun startControlOnPartnerReport() {
        every { startControlReport.startControl(19, 320) } returns ReportStatus.InControl
        assertThat(controller.startControlOnPartnerReport(19, 320)).isEqualTo(ReportStatusDTO.InControl)
    }

    @Test
    fun downloadAttachment() {
        val fileContentArray = ByteArray(5)
        every { downloadPartnerReportFile.download(partnerId = 20L, fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadAttachment(partnerId = 20L, fileId = 350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun deleteAttachment() {
        every { deletePartnerReportFile.delete(partnerId = 24L, fileId = 300L) } answers { }
        controller.deleteAttachment(partnerId = 24L, fileId = 300L)
        verify(exactly = 1) { deletePartnerReportFile.delete(partnerId = 24L, fileId = 300L) }
    }

    @Test
    fun uploadAttachment() {
        val slotFile = slot<ProjectFile>()
        every { uploadPartnerReportFile.uploadToReport(27L, reportId = 35L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadAttachment(27L, 35L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun listAttachments() {
        val searchRequest = slot<ProjectReportFileSearchRequest>()
        every { listPartnerReportFile.list(29L, Pageable.unpaged(), capture(searchRequest)) } returns
            PageImpl(listOf(reportFile))

        val searchRequestDto = ProjectReportFileSearchRequestDTO(
            reportId = 80L,
            treeNode = ProjectPartnerReportFileTypeDTO.PartnerReport,
            filterSubtypes = setOf(ProjectPartnerReportFileTypeDTO.Activity),
        )

        assertThat(controller.listAttachments(29L, Pageable.unpaged(), searchRequestDto).content)
            .containsExactly(reportFileDto)
        assertThat(searchRequest.captured).isEqualTo(
            ProjectReportFileSearchRequest(
                reportId = 80L,
                treeNode = ProjectPartnerReportFileType.PartnerReport,
                filterSubtypes = setOf(ProjectPartnerReportFileType.Activity),
            )
        )
    }

}
