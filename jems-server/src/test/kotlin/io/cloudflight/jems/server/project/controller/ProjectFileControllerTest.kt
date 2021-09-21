package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.PARTNER_ID
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.project.service.file.delete_project_file.DeleteProjectFileException
import io.cloudflight.jems.server.project.service.file.delete_project_file.DeleteProjectFileInteractor
import io.cloudflight.jems.server.project.service.file.download_project_file.DownloadProjectFileExceptions
import io.cloudflight.jems.server.project.service.file.download_project_file.DownloadProjectFileInteractor
import io.cloudflight.jems.server.utils.file
import io.cloudflight.jems.server.utils.fileByteArray
import io.cloudflight.jems.server.utils.fileMetadata
import io.cloudflight.jems.server.project.service.file.list_project_file_metadata.ListProjectFileMetadataExceptions
import io.cloudflight.jems.server.project.service.file.list_project_file_metadata.ListProjectFileMetadataInteractor
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.utils.projectFileCategoryDTO
import io.cloudflight.jems.server.project.service.file.set_project_file_description.SetProjectFileDescriptionExceptions
import io.cloudflight.jems.server.project.service.file.set_project_file_description.SetProjectFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.file.upload_project_file.UploadFileExceptions
import io.cloudflight.jems.server.project.service.file.upload_project_file.UploadProjectFileInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

internal class ProjectFileControllerTest : UnitTest() {

    @MockK
    lateinit var uploadProjectFile: UploadProjectFileInteractor

    @MockK
    lateinit var downloadProjectFile: DownloadProjectFileInteractor

    @MockK
    lateinit var deleteProjectFile: DeleteProjectFileInteractor

    @MockK
    lateinit var listProjectFileMetadata: ListProjectFileMetadataInteractor

    @MockK
    lateinit var setProjectFileDescription: SetProjectFileDescriptionInteractor

    @InjectMockKs
    lateinit var projectFileController: ProjectFileController

    @Nested
    inner class Upload {
        @Test
        fun `should upload file when there is no problem`() {
            val fileMetadata = fileMetadata()
            val categorySlot = slot<ProjectFileCategory>()
            val projectFileSlot = slot<ProjectFile>()
            every {
                uploadProjectFile.upload(PROJECT_ID, capture(categorySlot), capture(projectFileSlot))
            } returns fileMetadata

            assertThat(
                projectFileController.uploadFile(
                    PROJECT_ID,
                    projectFileCategoryDTO(ProjectFileCategoryTypeDTO.PARTNER, PARTNER_ID),
                    file
                )
            )
                .isEqualTo(fileMetadata.toDTO())
            assertThat(categorySlot.captured.type).isEqualTo(ProjectFileCategoryType.PARTNER)
            assertThat(categorySlot.captured.id).isEqualTo(PARTNER_ID)
            assertThat(projectFileSlot.captured.name).isEqualTo(file.originalFilename ?: file.name)
            assertThat(projectFileSlot.captured.size).isEqualTo(file.size)
        }

        @Test
        fun `should throw UploadFileExceptions when there is a problem in uploading file`() {
            every { uploadProjectFile.upload(PROJECT_ID, any(), any()) } throws UploadFileExceptions(RuntimeException())
            assertThrows<UploadFileExceptions> {
                projectFileController.uploadFile(PROJECT_ID, projectFileCategoryDTO(ProjectFileCategoryTypeDTO.PARTNER, PARTNER_ID), file)
            }
        }
    }

    @Nested
    inner class ListProjectFiles {
        @Test
        fun `should list file metadata when there is no problem`() {
            val fileMetadata = fileMetadata()
            val categorySlot = slot<ProjectFileCategory>()
            every {
                listProjectFileMetadata.list(PROJECT_ID, capture(categorySlot), Pageable.unpaged())
            } returns PageImpl(mutableListOf(fileMetadata))

            assertThat(projectFileController.listProjectFiles(PROJECT_ID, projectFileCategoryDTO(), Pageable.unpaged()))
                .isEqualTo(PageImpl(mutableListOf(fileMetadata.toDTO())))
            assertThat(categorySlot.captured.type).isEqualTo(ProjectFileCategoryType.PARTNER)
            assertThat(categorySlot.captured.id).isEqualTo(PARTNER_ID)
        }

        @Test
        fun `should throw ListProjectFileMetadataExceptions when there is a problem in listing file metadata`() {
            every { listProjectFileMetadata.list(PROJECT_ID, any(), any()) } throws ListProjectFileMetadataExceptions(
                RuntimeException()
            )
            assertThrows<ListProjectFileMetadataExceptions> {
                projectFileController.listProjectFiles(PROJECT_ID, projectFileCategoryDTO(), Pageable.unpaged())
            }
        }
    }

    @Nested
    inner class SetDescription {
        @Test
        fun `should set file description when there is no problem`() {
            val description = "desc"
            val fileMetadata = fileMetadata(description = description)
            every {
                setProjectFileDescription.setDescription(PROJECT_ID, FILE_ID, description)
            } returns fileMetadata

            assertThat(projectFileController.setProjectFileDescription(PROJECT_ID, FILE_ID, description))
                .isEqualTo(fileMetadata.toDTO())
        }

        @Test
        fun `should throw SetProjectFileDescriptionExceptions when there is a problem in setting descriptio for file`() {
            every {
                setProjectFileDescription.setDescription(PROJECT_ID, any(), any())
            } throws SetProjectFileDescriptionExceptions(RuntimeException())
            assertThrows<SetProjectFileDescriptionExceptions> {
                projectFileController.setProjectFileDescription(PROJECT_ID, FILE_ID, "desc")
            }
        }
    }

    @Nested
    inner class DeleteProjectFile {
        @Test
        fun `should delete file when there is no problem`() {
            every { deleteProjectFile.delete(PROJECT_ID, FILE_ID) } returns Unit
            assertThat(projectFileController.deleteProjectFile(PROJECT_ID, FILE_ID))
                .isEqualTo(Unit)
        }

        @Test
        fun `should throw DeleteProjectFileException when there is a problem in deletion of file`() {
            every {
                deleteProjectFile.delete(PROJECT_ID, any())
            } throws DeleteProjectFileException(RuntimeException())
            assertThrows<DeleteProjectFileException> {
                projectFileController.deleteProjectFile(PROJECT_ID, FILE_ID)
            }
        }
    }

    @Nested
    inner class DownloadFile {
        @Test
        fun `should download file when there is no problem`() {
            val fileMetadata = fileMetadata()
            every { downloadProjectFile.download(PROJECT_ID, FILE_ID) } returns Pair(fileMetadata, fileByteArray)
            assertThat(projectFileController.downloadFile(PROJECT_ID, FILE_ID))
                .isEqualTo(
                    ResponseEntity.ok()
                        .contentLength(fileMetadata.size)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${fileMetadata.name}\"")
                        .body(ByteArrayResource(fileByteArray))
                )
        }

        @Test
        fun `should throw DownloadProjectFileExceptions when there is a problem in downloading file`() {
            every {
                downloadProjectFile.download(PROJECT_ID, FILE_ID)
            } throws DownloadProjectFileExceptions(RuntimeException())
            assertThrows<DownloadProjectFileExceptions> {
                projectFileController.downloadFile(PROJECT_ID, FILE_ID)
            }
        }
    }
}
