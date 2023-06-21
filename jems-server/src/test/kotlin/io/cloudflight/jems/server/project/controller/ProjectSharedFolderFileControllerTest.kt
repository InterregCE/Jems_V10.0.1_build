package io.cloudflight.jems.server.project.controller

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
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.DeleteFileFromSharedFolder
import io.cloudflight.jems.server.project.service.sharedFolderFile.description.SetDescriptionToSharedFolderFile
import io.cloudflight.jems.server.project.service.sharedFolderFile.download.DownloadSharedFolderFile
import io.cloudflight.jems.server.project.service.sharedFolderFile.list.ListSharedFolderFiles
import io.cloudflight.jems.server.project.service.sharedFolderFile.upload.UploadFileToSharedFolder
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime


internal class ProjectSharedFolderFileControllerTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val FILE_ID = 5L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
    }

    private val shareFolderFile = JemsFile(
        id = FILE_ID,
        name = "shared-folder-file.pdf",
        type = JemsFileType.ProjectReport,
        uploaded = YESTERDAY,
        author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
        size = 47889L,
        description = "desc"
    )

    private val jemsFileDTO = JemsFileDTO(
        id = FILE_ID,
        name = "shared-folder-file.pdf",
        type = JemsFileTypeDTO.ProjectReport,
        uploaded = YESTERDAY,
        author = UserSimpleDTO(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
        size = 47889L,
        sizeString = "46.8\u0020kB",
        description = "desc"
    )

    @MockK
    lateinit var listSharedFolderFiles: ListSharedFolderFiles

    @MockK
    lateinit var uploadFileToSharedFolder: UploadFileToSharedFolder

    @MockK
    lateinit var setDescriptionToSharedFolderFile: SetDescriptionToSharedFolderFile

    @MockK
    lateinit var deleteFileFromSharedFolder: DeleteFileFromSharedFolder

    @MockK
    lateinit var downloadSharedFolderFile: DownloadSharedFolderFile

    @InjectMockKs
    lateinit var controller: ProjectSharedFolderFileController

    @Test
    fun listSharedFolderFiles() {
        every { listSharedFolderFiles.list(PROJECT_ID, Pageable.unpaged()) } returns
            PageImpl(listOf(shareFolderFile))

        Assertions.assertThat(controller.listSharedFolderFiles(PROJECT_ID, Pageable.unpaged()).content)
            .containsExactly(jemsFileDTO)
    }

    @Test
    fun downloadSharedFolderFile() {
        val fileContentArray = ByteArray(5)
        every { downloadSharedFolderFile.download(PROJECT_ID, 5L) } returns Pair("shared-folder-file.pdf", fileContentArray)

        Assertions.assertThat(controller.downloadSharedFolderFile(PROJECT_ID, 5L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shared-folder-file.pdf\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun deleteFileFromSharedFolder() {
        every { deleteFileFromSharedFolder.delete(PROJECT_ID, 5L) } answers { }

        controller.deleteSharedFolderFile(PROJECT_ID, 5L)
        verify(exactly = 1) { deleteFileFromSharedFolder.delete(PROJECT_ID, 5L) }
    }

    @Test
    fun setDescriptionToSharedFolderFile() {
        every { setDescriptionToSharedFolderFile.set(PROJECT_ID, FILE_ID, "update desc") } answers { }

        controller.setDescriptionToSharedFolderFile(PROJECT_ID, FILE_ID, "update desc")
        verify(exactly = 1) { setDescriptionToSharedFolderFile.set(PROJECT_ID, FILE_ID, "update desc") }
    }

    @Test
    fun uploadFileToSharedFolder() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToSharedFolder.upload(PROJECT_ID, capture(slotFile)) } returns dummyFile

        Assertions.assertThat(controller.uploadFileToSharedFolder(PROJECT_ID, dummyMultipartFile())).isEqualTo(dummyFileDto)
        Assertions.assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }
}
