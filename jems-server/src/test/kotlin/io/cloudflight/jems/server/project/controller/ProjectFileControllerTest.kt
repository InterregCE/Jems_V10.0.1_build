package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.file.FileMetadata
import io.cloudflight.jems.server.project.service.file.FileStorageService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile

class ProjectFileControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 577L
        private val FILE_CONTENT = "content".toByteArray()
    }

    @MockK
    lateinit var fileService: FileStorageService

    @InjectMockKs
    private lateinit var controller: ProjectFileController

    @BeforeEach
    fun resetFileService() {
        clearMocks(fileService)
    }

    @Test
    fun `save file applicant`() {
        val slot = slot<FileMetadata>()
        every { fileService.saveFile(any(), capture(slot)) } answers { }

        val file = MockMultipartFile("name.txt", "name.txt", "text/csv", FILE_CONTENT)
        controller.uploadProjectApplicationFile(PROJECT_ID, file)

        verify(exactly = 1) { fileService.saveFile(any(), any()) }
        assertThat(slot.captured).isEqualTo(FileMetadata(
            name = "name.txt",
            projectId = PROJECT_ID,
            size = 7,
            type = ProjectFileType.APPLICANT_FILE,
        ))
    }

    @Test
    fun `save file assessment`() {
        val slot = slot<FileMetadata>()
        every { fileService.saveFile(any(), capture(slot)) } answers { }

        val file = MockMultipartFile("name.txt", "name.txt", "text/csv", FILE_CONTENT)
        controller.uploadProjectAssessmentFile(PROJECT_ID, file)

        verify(exactly = 1) { fileService.saveFile(any(), any()) }
        assertThat(slot.captured).isEqualTo(FileMetadata(
            name = "name.txt",
            projectId = PROJECT_ID,
            size = 7,
            type = ProjectFileType.ASSESSMENT_FILE,
        ))
    }

    @Test
    fun `download file applicant`() {
        every { fileService.downloadFile(PROJECT_ID, 245L, ProjectFileType.APPLICANT_FILE) } returns
            Pair("name_download.txt", FILE_CONTENT)

        val response = controller.downloadProjectApplicationFile(PROJECT_ID, 245L)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.get(HttpHeaders.CONTENT_TYPE)).containsExactly(MediaType.APPLICATION_OCTET_STREAM_VALUE)
        assertThat(response.headers.get(HttpHeaders.CONTENT_DISPOSITION)).containsExactly("attachment; filename=\"name_download.txt\"")
        assertThat(response.body?.byteArray).isEqualTo(FILE_CONTENT)
    }

    @Test
    fun `download file assessment`() {
        every { fileService.downloadFile(PROJECT_ID, 280L, ProjectFileType.ASSESSMENT_FILE) } returns
            Pair("name_download.txt", FILE_CONTENT)

        val response = controller.downloadProjectAssessmentFile(PROJECT_ID, 280L)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.get(HttpHeaders.CONTENT_TYPE)).containsExactly(MediaType.APPLICATION_OCTET_STREAM_VALUE)
        assertThat(response.headers.get(HttpHeaders.CONTENT_DISPOSITION)).containsExactly("attachment; filename=\"name_download.txt\"")
        assertThat(response.body?.byteArray).isEqualTo(FILE_CONTENT)
    }

}
