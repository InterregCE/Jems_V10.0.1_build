package io.cloudflight.jems.server.project.controller.auditAndControl

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
import io.cloudflight.jems.server.project.service.auditAndControl.file.delete.DeleteAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.download.DownloadAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.list.ListAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription.UpdateDescriptionAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.upload.UploadAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
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

class ProjectAuditFileControllerTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 11L
        const val AUDIT_CONTROL_ID = 22L
        const val FILE_ID = 33L
        private val UPLOAD_DATE = ZonedDateTime.now().minusWeeks(4)

        private val auditControlFile = JemsFile(
            id = 123L,
            name = "audit-control.pdf",
            type = JemsFileType.AuditControl,
            uploaded = UPLOAD_DATE,
            author = UserSimple(44L, email = "admin@jems.eu", name = "admin", surname = "admin"),
            size = 1024L,
            description = "csed",
            indexedPath = "index/path"
        )

        private val auditControlFileDto = JemsFileDTO(
            id = 123L,
            name = "audit-control.pdf",
            type = JemsFileTypeDTO.AuditControl,
            uploaded = UPLOAD_DATE,
            author = UserSimpleDTO(44L, email = "admin@jems.eu", name = "admin", surname = "admin"),
            size = 1024L,
            sizeString = (1024L).sizeToString(),
            description = "csed"
        )
    }

    @MockK
    lateinit var listAuditControlFileInteractor: ListAuditControlFileInteractor

    @MockK
    lateinit var uploadAuditControlFileInteractor: UploadAuditControlFileInteractor

    @MockK
    lateinit var updateDescriptionAuditControlFileInteractor: UpdateDescriptionAuditControlFileInteractor

    @MockK
    lateinit var downloadAuditControlFileInteractor: DownloadAuditControlFileInteractor

    @MockK
    lateinit var deleteAuditControlFileInteractor: DeleteAuditControlFileInteractor

    @InjectMockKs
    lateinit var controller: ProjectAuditFileController

    @BeforeEach
    fun setup() {
        clearMocks(
            listAuditControlFileInteractor,
            uploadAuditControlFileInteractor,
            updateDescriptionAuditControlFileInteractor,
            downloadAuditControlFileInteractor,
            deleteAuditControlFileInteractor,
        )
    }


    @Test
    fun list() {
        every { listAuditControlFileInteractor.list(AUDIT_CONTROL_ID, Pageable.unpaged()) } returns PageImpl(listOf(
            auditControlFile
        ))

        assertThat(controller.list(PROJECT_ID, AUDIT_CONTROL_ID, Pageable.unpaged()).content)
            .containsExactly(auditControlFileDto)
        verify { listAuditControlFileInteractor.list(AUDIT_CONTROL_ID, Pageable.unpaged()) }
    }

    @Test
    fun upload() {
        val slotFile = slot<ProjectFile>()
        every { uploadAuditControlFileInteractor.upload(AUDIT_CONTROL_ID, capture(slotFile)) } returns dummyFile

        assertThat(controller.upload(PROJECT_ID, AUDIT_CONTROL_ID, dummyMultipartFile()))
            .isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun updateDescription() {
        every { updateDescriptionAuditControlFileInteractor.updateDescription(AUDIT_CONTROL_ID, FILE_ID, "new desc") } answers { }

        assertDoesNotThrow { controller.updateDescription(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID, "new desc") }
        verify { updateDescriptionAuditControlFileInteractor.updateDescription(AUDIT_CONTROL_ID, FILE_ID, "new desc") }
    }

    @Test
    fun download() {
        val fileContentArray = ByteArray(5)
        every { downloadAuditControlFileInteractor.download(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.download(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID))
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
        every { deleteAuditControlFileInteractor.delete(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) } answers { }

        assertDoesNotThrow { controller.delete(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) }
        verify { deleteAuditControlFileInteractor.delete(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) }
    }

}
