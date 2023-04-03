package io.cloudflight.jems.server.project.controller.report.project.annexes

import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileSearchRequestDTO
import io.cloudflight.jems.api.project.dto.report.file.UserSimpleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.report.partner.dummyFile
import io.cloudflight.jems.server.project.controller.report.partner.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.partner.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.partner.dummyMultipartFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.project.annexes.delete.DeleteProjectReportAnnexesFileInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.download.DownloadProjectReportAnnexesFileInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.list.ListProjectReportAnnexesInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.update.SetDescriptionToProjectReportFileInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.upload.UploadProjectReportAnnexesFileInteractor
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
import java.time.ZonedDateTime

internal class ProjectReportAnnexesControllerTest : UnitTest() {

    companion object {
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
    }

    private val projectReportFile = JemsFile(
        id = 5L,
        name = "attachment.pdf",
        type = JemsFileType.ProjectReport,
        uploaded = YESTERDAY,
        author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
        size = 47889L,
        description = "desc"
    )

    private val projectReportFileDTO = ProjectReportFileDTO(
        id = 5L,
        name = "attachment.pdf",
        type = ProjectPartnerReportFileTypeDTO.ProjectReport,
        uploaded = YESTERDAY,
        author = UserSimpleDTO(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
        size = 47889L,
        sizeString = "46.8\u0020kB",
        description = "desc"
    )

    @MockK
    lateinit var listProjectReportAnnexes: ListProjectReportAnnexesInteractor

    @MockK
    lateinit var uploadProjectReportAnnexesFile: UploadProjectReportAnnexesFileInteractor

    @MockK
    lateinit var setDescriptionToProjectReportFile: SetDescriptionToProjectReportFileInteractor

    @MockK
    lateinit var deleteProjectReportAnnexesFile: DeleteProjectReportAnnexesFileInteractor

    @MockK
    lateinit var downloadProjectReportAnnexesFile: DownloadProjectReportAnnexesFileInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportAnnexesController

    @Test
    fun listProjectReportAnnexes() {
        val searchRequest = slot<JemsFileSearchRequest>()
        every { listProjectReportAnnexes.list(1L, 2L, Pageable.unpaged(), capture(searchRequest)) } returns
                PageImpl(listOf(projectReportFile))

        val searchRequestDto = ProjectReportFileSearchRequestDTO(
            reportId = 2L,
            treeNode = ProjectPartnerReportFileTypeDTO.ProjectReport,
            filterSubtypes = emptySet(),
        )

        assertThat(controller.getProjectReportAnnexes(1L, 2L, Pageable.unpaged(), searchRequestDto).content)
            .containsExactly(projectReportFileDTO)
        assertThat(searchRequest.captured).isEqualTo(
            JemsFileSearchRequest(
                reportId = 2L,
                treeNode = JemsFileType.ProjectReport,
                filterSubtypes = emptySet(),
            )
        )
    }

    @Test
    fun downloadProjectReportAnnexesFile() {
        val fileContentArray = ByteArray(5)
        every { downloadProjectReportAnnexesFile.download(1L, 2L, 5L) } returns Pair("attachment.pdf", fileContentArray)

        assertThat(controller.downloadProjectReportAnnexesFile(1L, 2L, 5L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attachment.pdf\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun deleteProjectReportAnnexesFile() {
        every { deleteProjectReportAnnexesFile.delete(1L, 2L, 5L) } answers { }

        controller.deleteProjectReportAnnexesFile(1L, 2L, 5L)
        verify(exactly = 1) { deleteProjectReportAnnexesFile.delete(1L, 2L, 5L) }
    }

    @Test
    fun updateProjectReportAnnexesFileDescription() {
        every { setDescriptionToProjectReportFile.update(1L, 2L, 5L, "update desc") } answers { }

        controller.updateProjectReportAnnexesFileDescription(1L, 2L, 5L, "update desc")
        verify(exactly = 1) { setDescriptionToProjectReportFile.update(1L, 2L, 5L, "update desc") }
    }

    @Test
    fun uploadProjectReportAnnexesFile() {
        val slotFile = slot<ProjectFile>()
        every { uploadProjectReportAnnexesFile.upload(1L, 2L, capture(slotFile)) } returns dummyFile

        assertThat(controller.uploadProjectReportAnnexesFile(1L, 2L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }
}
