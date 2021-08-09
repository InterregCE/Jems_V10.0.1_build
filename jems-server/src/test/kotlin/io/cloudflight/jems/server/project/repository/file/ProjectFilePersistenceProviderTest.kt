package io.cloudflight.jems.server.project.repository.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFileEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.FILE_SIZE
import io.cloudflight.jems.server.utils.INVESTMENT_ID
import io.cloudflight.jems.server.utils.PARTNER_ID
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.fileByteArray
import io.cloudflight.jems.server.utils.fileMetadata
import io.cloudflight.jems.server.utils.projectEntity
import io.cloudflight.jems.server.utils.projectFile
import io.cloudflight.jems.server.utils.projectFileCategory
import io.cloudflight.jems.server.utils.projectFileCategoryEntity
import io.cloudflight.jems.server.utils.projectFileEntity
import io.cloudflight.jems.server.utils.userEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

internal class ProjectFilePersistenceProviderTest : UnitTest() {

    private val projectFilesBucket = "project-files"

    @MockK
    lateinit var storage: MinioStorage

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectFileRepository: ProjectFileRepository

    @MockK
    lateinit var projectFileCategoryRepository: ProjectFileCategoryRepository

    @InjectMockKs
    lateinit var projectFilePersistenceProvider: ProjectFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(storage)
    }

    @Nested
    inner class SaveFile {
        @Test
        fun `should save file in the storage when there is no problem`() {
            val objectPathSlot = slot<String>()
            every {
                storage.saveFile(
                    projectFilesBucket, capture(objectPathSlot), FILE_SIZE, projectFile.stream
                )
            } returns Unit
            projectFilePersistenceProvider.saveFile(PROJECT_ID, FILE_ID, USER_ID, projectFile)
            verify(exactly = 1) {
                storage.saveFile(
                    projectFilesBucket, objectPathSlot.captured, FILE_SIZE, projectFile.stream
                )
            }
        }

        @Test
        fun `should use correct object path when saving file in the storage`() {
            val objectPathSlot = slot<String>()
            every {
                storage.saveFile(
                    projectFilesBucket, capture(objectPathSlot), FILE_SIZE, projectFile.stream
                )
            } returns Unit
            projectFilePersistenceProvider.saveFile(PROJECT_ID, FILE_ID, USER_ID, projectFile)
            assertThat(objectPathSlot.captured).isEqualTo("project-$PROJECT_ID/$FILE_ID/$FILE_NAME")
        }
    }

    @Nested
    inner class SaveFileMetadata {
        @Test
        fun `should save file metadata when there is no problem`() {
            val fileEntitySlot = slot<ProjectFileEntity>()
            val savedFileEntity = projectFileEntity(FILE_ID)
            val categoryEntitySlot = slot<List<ProjectFileCategoryEntity>>()
            every { projectRepository.getOne(PROJECT_ID) } returns projectEntity
            every { userRepository.getOne(USER_ID) } returns userEntity
            every { projectFileRepository.save(capture(fileEntitySlot)) } returns savedFileEntity
            every { projectFileCategoryRepository.saveAll(capture(categoryEntitySlot)) } returnsArgument 0
            projectFilePersistenceProvider.saveFileMetadata(
                PROJECT_ID, USER_ID, projectFile, projectFileCategory()
            )

            assertThat(fileEntitySlot.captured.name).isEqualTo(projectFile.name)
            assertThat(fileEntitySlot.captured.user.id).isEqualTo(USER_ID)
            assertThat(fileEntitySlot.captured.project.id).isEqualTo(PROJECT_ID)
            assertThat(fileEntitySlot.captured.size).isEqualTo(projectFile.size)
            assertThat(fileEntitySlot.captured.description).isNull()

        }

        @TestFactory
        fun `should save file categories correctly when saving a file in a particular category`() =
            listOf(
                Pair(
                    projectFileCategory(ProjectFileCategoryType.ASSESSMENT, null),
                    listOf(ProjectFileCategoryType.ASSESSMENT.name)
                ),
                Pair(
                    projectFileCategory(ProjectFileCategoryType.PARTNER, PARTNER_ID),
                    listOf(
                        ProjectFileCategoryType.APPLICATION.name, ProjectFileCategoryType.PARTNER.name,
                        "${ProjectFileCategoryType.PARTNER.name}=$PARTNER_ID"
                    )
                ),
                Pair(
                    projectFileCategory(ProjectFileCategoryType.INVESTMENT, INVESTMENT_ID),
                    listOf(
                        ProjectFileCategoryType.APPLICATION.name, ProjectFileCategoryType.INVESTMENT.name,
                        "${ProjectFileCategoryType.INVESTMENT.name}=$INVESTMENT_ID"
                    )
                ),
            ).map { input ->
                DynamicTest.dynamicTest(
                    "should save file categories correctly when saving a file in ${input.first} category"
                ) {
                    val savedFileEntity = projectFileEntity(FILE_ID)
                    val categoryEntitySlot = slot<List<ProjectFileCategoryEntity>>()
                    every { projectRepository.getOne(PROJECT_ID) } returns projectEntity
                    every { userRepository.getOne(USER_ID) } returns userEntity
                    every { projectFileRepository.save(any()) } returns savedFileEntity
                    every { projectFileCategoryRepository.saveAll(capture(categoryEntitySlot)) } returnsArgument 0
                    projectFilePersistenceProvider.saveFileMetadata(
                        PROJECT_ID, USER_ID, projectFile, input.first
                    )
                    assertThat(categoryEntitySlot.captured.size).isEqualTo(input.second.size)
                    assertThat(categoryEntitySlot.captured.map { it.categoryId.type }).containsAll(input.second)
                    assertThat(categoryEntitySlot.captured.all { it.categoryId.fileId == FILE_ID }).isTrue
                    assertThat(categoryEntitySlot.captured.all { it.projectFile == savedFileEntity }).isTrue
                }
            }
    }

    @Nested
    inner class GetFileMetadata {

        @Test
        fun `should return file metadata when there is no problem`() {
            val fileMetadata = fileMetadata()
            every { projectFileRepository.findById(FILE_ID) } returns Optional.of(
                projectFileEntity(fileMetadata.id, fileMetadata.uploadedAt)
            )
            assertThat(projectFilePersistenceProvider.getFileMetadata(FILE_ID)).isEqualTo(fileMetadata)
        }

        @Test
        fun `should throw ProjectFileNotFoundException when getting file metadata while file metadata does not exist`() {
            every { projectFileRepository.findById(FILE_ID) } returns Optional.empty()
            assertThrows<ProjectFileNotFoundException> { projectFilePersistenceProvider.getFileMetadata(FILE_ID) }
        }
    }

    @Test
    fun `should return category type set for a file when there is no problem`() {
        val projectFileEntity = projectFileEntity()
        every { projectFileCategoryRepository.findAllByCategoryIdFileId(FILE_ID) } returns listOf(
            projectFileCategoryEntity(
                FILE_ID, categoryTypeString = ProjectFileCategoryType.APPLICATION.name, projectFileEntity
            ),
            projectFileCategoryEntity(
                FILE_ID, categoryTypeString = ProjectFileCategoryType.PARTNER.name, projectFileEntity
            ),
            projectFileCategoryEntity(
                FILE_ID, categoryTypeString = "${ProjectFileCategoryType.PARTNER.name}=$PARTNER_ID", projectFileEntity
            ),
        )
        assertThat(projectFilePersistenceProvider.getFileCategoryTypeSet(FILE_ID)).containsAll(
            listOf(ProjectFileCategoryType.APPLICATION, ProjectFileCategoryType.PARTNER)
        )
    }

    @Nested
    inner class ListFileMetadata {
        @Test
        fun `should return list of file metadata when list files under a category other than ALL`() {
            val fileMetadata = fileMetadata()
            every {
                projectFileRepository.findAllProjectFilesInCategory(PROJECT_ID, any(), Pageable.unpaged())
            } returns PageImpl(
                mutableListOf(projectFileEntity(FILE_ID, fileMetadata.uploadedAt))
            )

            assertThat(
                projectFilePersistenceProvider.listFileMetadata(PROJECT_ID, projectFileCategory(), Pageable.unpaged())
            ).containsExactly(fileMetadata)
        }

        @Test
        fun `should return list of file metadata when list files in ALL category`() {
            val fileMetadata = fileMetadata()
            every {
                projectFileRepository.findAllByProjectId(PROJECT_ID, Pageable.unpaged())
            } returns PageImpl(
                mutableListOf(projectFileEntity(FILE_ID, fileMetadata.uploadedAt))
            )

            assertThat(
                projectFilePersistenceProvider.listFileMetadata(
                    PROJECT_ID, projectFileCategory(ProjectFileCategoryType.ALL, null), Pageable.unpaged()
                )
            ).containsExactly(fileMetadata)
        }

        @TestFactory
        fun `should extract file category type string correctly when listing file metadata for a particular category`() =
            listOf(
                projectFileCategory(categoryType = ProjectFileCategoryType.APPLICATION, categoryId = null),
                projectFileCategory(categoryType = ProjectFileCategoryType.PARTNER, categoryId = null),
                projectFileCategory(categoryType = ProjectFileCategoryType.INVESTMENT, categoryId = null),
                projectFileCategory(categoryType = ProjectFileCategoryType.PARTNER, categoryId = PARTNER_ID),
                projectFileCategory(categoryType = ProjectFileCategoryType.INVESTMENT, categoryId = INVESTMENT_ID),
                projectFileCategory(categoryType = ProjectFileCategoryType.ASSESSMENT, categoryId = null),
            ).map {
                Pair(it, if (it.id == null) it.type.name else "${it.type.name}=${it.id}")
            }.map { input ->
                DynamicTest.dynamicTest(
                    "should extract file category type string correctly when listing file metadata for ${input.second}"
                ) {
                    val categoryTypeSlot = slot<String>()
                    every {
                        projectFileRepository.findAllProjectFilesInCategory(
                            PROJECT_ID, capture(categoryTypeSlot), Pageable.unpaged()
                        )
                    } returns PageImpl(mutableListOf(projectFileEntity(FILE_ID)))
                    projectFilePersistenceProvider.listFileMetadata(PROJECT_ID, input.first, Pageable.unpaged())

                    assertThat(categoryTypeSlot.captured).isEqualTo(input.second)
                }
            }
    }

    @Nested
    inner class ThrowIfFileNameExistsInCategory {
        @Test
        fun `should throw FileNameAlreadyExistsException when file name already exists in the project for a category other than ALL`() {
            every { projectFileCategoryRepository.fileNameExistsInCategory(FILE_ID, FILE_NAME, any()) } returns true
            assertThrows<FileNameAlreadyExistsException> {
                projectFilePersistenceProvider.throwIfFileNameExistsInCategory(
                    FILE_ID, FILE_NAME, projectFileCategory()
                )
            }
        }

        @Test
        fun `should return Unit when file name does not exist in the project for a category other than ALL`() {
            every { projectFileCategoryRepository.fileNameExistsInCategory(FILE_ID, FILE_NAME, any()) } returns false
            assertThat(
                projectFilePersistenceProvider.throwIfFileNameExistsInCategory(
                    FILE_ID, FILE_NAME, projectFileCategory()
                )
            ).isEqualTo(Unit)
        }

        @Test
        fun `should throw FileNameAlreadyExistsException when file name already exists in the project for ALL category`() {
            every {
                projectFileCategoryRepository.existsByProjectFileProjectIdAndProjectFileName(FILE_ID, FILE_NAME)
            } returns true
            assertThrows<FileNameAlreadyExistsException> {
                projectFilePersistenceProvider.throwIfFileNameExistsInCategory(
                    FILE_ID,
                    FILE_NAME,
                    projectFileCategory(ProjectFileCategoryType.ALL, null)
                )
            }
        }

        @Test
        fun `should return Unit when file name does not exist in the project for ALL category`() {
            every {
                projectFileCategoryRepository.existsByProjectFileProjectIdAndProjectFileName(FILE_ID, FILE_NAME)
            } returns false
            assertThat(
                projectFilePersistenceProvider.throwIfFileNameExistsInCategory(
                    FILE_ID, FILE_NAME, projectFileCategory(ProjectFileCategoryType.ALL, null)
                )
            ).isEqualTo(Unit)
        }
    }

    @Test
    fun `should return file when there is no problem`() {
        every { storage.getFile(projectFilesBucket, any()) } returns fileByteArray
        assertThat(
            projectFilePersistenceProvider.getFile(PROJECT_ID, FILE_ID, FILE_NAME)
        ).isEqualTo(fileByteArray)
    }

    @Test
    fun `should delete file when there is no problem`() {
        every { projectFileCategoryRepository.deleteAllByCategoryIdFileId(FILE_ID) } returns Unit
        every { projectFileRepository.deleteById(FILE_ID) } returns Unit
        every { storage.deleteFile(projectFilesBucket, any()) } returns Unit
        assertThat(
            projectFilePersistenceProvider.deleteFile(PROJECT_ID, FILE_ID, FILE_NAME)
        ).isEqualTo(Unit)
    }

    @Nested
    inner class SetFileDescription {
        @Test
        fun `should set description for file when there is no problem`() {
            val projectFileEntity = projectFileEntity(FILE_ID)
            val description = "desc"
            every { projectFileRepository.findById(FILE_ID) } returns Optional.of(projectFileEntity)
            assertThat(projectFilePersistenceProvider.setFileDescription(FILE_ID, description))
                .isEqualTo(fileMetadata(uploadedAt = projectFileEntity.updated, description))

        }

        @Test
        fun `should throw ProjectFileNotFoundException when setting file description while file metadata does not exist`() {
            every { projectFileRepository.findById(FILE_ID) } throws ProjectFileNotFoundException()
            assertThrows<ProjectFileNotFoundException> {
                projectFilePersistenceProvider.setFileDescription(FILE_ID, "desc")
            }
        }
    }
}
