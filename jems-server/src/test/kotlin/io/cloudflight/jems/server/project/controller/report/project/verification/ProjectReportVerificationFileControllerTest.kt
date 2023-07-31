package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO
import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.controller.report.partner.dummyFile
import io.cloudflight.jems.server.project.controller.report.partner.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.partner.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.partner.dummyMultipartFile
import io.cloudflight.jems.server.project.controller.report.partner.sizeToString
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.project.verification.file.delete.DeleteProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.download.DownloadProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.list.ListProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.updateDescription.UpdateDescriptionProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.upload.UploadProjectReportVerificationFileInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime


class ProjectReportVerificationFileControllerTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 1L
        const val REPORT_ID = 2L
        const val FILE_ID = 3L
        private val UPLOAD_DATE = ZonedDateTime.now().minusWeeks(1)

        private val verificationFile = JemsFile(
            id = 179L,
            name = "document.pdf",
            type = JemsFileType.VerificationDocument,
            uploaded = UPLOAD_DATE,
            author = UserSimple(41L, email = "admin@jems.eu", name = "admin", surname = "admin"),
            size = 1024L,
            description = "desc",
            indexedPath = "index/path"
        )

        private val verificationFileDto = JemsFileDTO(
            id = 179L,
            name = "document.pdf",
            type = JemsFileTypeDTO.VerificationDocument,
            uploaded = UPLOAD_DATE,
            author = UserSimpleDTO(41L, email = "admin@jems.eu", name = "admin", surname = "admin"),
            size = 1024L,
            sizeString = (1024L).sizeToString(),
            description = "desc"
        )
    }

    @MockK
    lateinit var listProjectReportVerificationFile: ListProjectReportVerificationFileInteractor

    @MockK
    lateinit var uploadProjectReportVerificationFile: UploadProjectReportVerificationFileInteractor

    @MockK
    lateinit var updateDescriptionProjectReportVerificationFile: UpdateDescriptionProjectReportVerificationFileInteractor

    @MockK
    lateinit var downloadProjectReportVerificationFile: DownloadProjectReportVerificationFileInteractor

    @MockK
    lateinit var deleteProjectReportVerificationFile: DeleteProjectReportVerificationFileInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportVerificationFileController

    @BeforeEach
    fun setup() {
        clearMocks(
            listProjectReportVerificationFile,
            uploadProjectReportVerificationFile,
            updateDescriptionProjectReportVerificationFile,
            downloadProjectReportVerificationFile,
            deleteProjectReportVerificationFile,
        )
    }

    @Test
    fun list() {
        every { listProjectReportVerificationFile.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()) } returns PageImpl(listOf(verificationFile))

        assertThat(controller.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()).content)
            .containsExactly(verificationFileDto)
        verify { listProjectReportVerificationFile.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()) }
    }

    @Test
    fun upload() {
        val slotFile = slot<ProjectFile>()
        every { uploadProjectReportVerificationFile.upload(PROJECT_ID, REPORT_ID, capture(slotFile)) } returns dummyFile

        assertThat(controller.upload(PROJECT_ID, REPORT_ID, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun updateDescription() {
        every { updateDescriptionProjectReportVerificationFile.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "new desc") } answers { }

        assertDoesNotThrow { controller.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "new desc") }
        verify { updateDescriptionProjectReportVerificationFile.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "new desc") }
    }

    @Test
    fun download() {
        val fileContentArray = ByteArray(5)
        every { downloadProjectReportVerificationFile.download(PROJECT_ID, REPORT_ID, FILE_ID) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.download(PROJECT_ID, REPORT_ID, FILE_ID))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun delete() {
        every { deleteProjectReportVerificationFile.delete(PROJECT_ID, REPORT_ID, FILE_ID) } answers { }

        assertDoesNotThrow { controller.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
        verify { deleteProjectReportVerificationFile.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }

}
