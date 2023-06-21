package io.cloudflight.jems.server.project.service.file.list_project_file_metadata

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.authorization.ProjectFileAuthorization
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType.ALL
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType.ASSESSMENT
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType.APPLICATION
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType.MODIFICATION
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType.PARTNER
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType.INVESTMENT
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.utils.fileMetadata
import io.cloudflight.jems.server.utils.projectFileCategory
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

internal class ListProjectFileMetadataTest : UnitTest() {

    @MockK
    lateinit var filePersistence: ProjectFilePersistence

    @MockK
    lateinit var authorization: ProjectFileAuthorization

    @InjectMockKs
    lateinit var listProjectFileMetadata: ListProjectFileMetadata

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence, authorization)
    }

    @Test
    fun `should return list of project files metadata`() {
        val category = projectFileCategory()
        val fileMetadata = fileMetadata()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf(category.type)
        every {
            filePersistence.listFileMetadata(PROJECT_ID, category, Pageable.unpaged())
        } returns PageImpl(mutableListOf(fileMetadata))
        every { filePersistence.getCategoriesMap(setOf(FILE_ID)) } returns mapOf(FILE_ID to setOf(PARTNER))

        assertThat(listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged()))
            .containsExactly(fileMetadata.copy(category = PARTNER))
    }

    @Test
    fun `should return empty page when user does not have access to retrieve file from a category other than ALL`() {
        val category = projectFileCategory()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf()
        assertThat(listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged()))
            .isEmpty()
    }

    @Test
    fun `should return empty page when listing files in ALL categories and user does not have access to retrieve file from any category`() {
        val category = projectFileCategory(ProjectFileCategoryType.ALL, null)
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf()
        assertThat(listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged()))
            .isEmpty()
    }

    @Test
    fun `should list only ASSESSMENT files when list files in ALL category and user does not have access to APPLICATION files`() {
        val category = projectFileCategory(ProjectFileCategoryType.ALL, null)
        val categorySlot = slot<ProjectFileCategory>()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf(ProjectFileCategoryType.ASSESSMENT)
        every {
            filePersistence.listFileMetadata(PROJECT_ID, capture(categorySlot), Pageable.unpaged())
        } returns PageImpl(mutableListOf())
        every { filePersistence.getCategoriesMap(any()) } returns emptyMap()
        listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged())
        assertThat(categorySlot.captured).isEqualTo(ProjectFileCategory(ProjectFileCategoryType.ASSESSMENT, null))
    }

    @Test
    fun `should list only APPLICATION files when list files in ALL category and user does not have access to ASSESSMENT files`() {
        val category = projectFileCategory(ProjectFileCategoryType.ALL, null)
        val categorySlot = slot<ProjectFileCategory>()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf(
            ProjectFileCategoryType.APPLICATION, ProjectFileCategoryType.INVESTMENT, ProjectFileCategoryType.PARTNER
        )
        every {
            filePersistence.listFileMetadata(PROJECT_ID, capture(categorySlot), Pageable.unpaged())
        } returns PageImpl(mutableListOf())
        every { filePersistence.getCategoriesMap(any()) } returns emptyMap()
        listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged())
        assertThat(categorySlot.captured).isEqualTo(ProjectFileCategory(ProjectFileCategoryType.APPLICATION, null))
    }

    @Test
    fun `should list ALL files when list files in ALL category and user has access to ASSESSMENT and APPLICATION files`() {
        val category = projectFileCategory(ProjectFileCategoryType.ALL, null)
        val categorySlot = slot<ProjectFileCategory>()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf(
            ProjectFileCategoryType.ASSESSMENT,
            ProjectFileCategoryType.APPLICATION,
            ProjectFileCategoryType.INVESTMENT,
            ProjectFileCategoryType.PARTNER
        )
        every {
            filePersistence.listFileMetadata(PROJECT_ID, capture(categorySlot), Pageable.unpaged())
        } returns PageImpl(mutableListOf())
        every { filePersistence.getCategoriesMap(any()) } returns emptyMap()
        listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged())
        assertThat(categorySlot.captured).isEqualTo(ProjectFileCategory(ProjectFileCategoryType.ALL, null))
    }

    @Test
    fun `test category retrievals`() {
        val time = ZonedDateTime.now()
        val fileMetadata = fileMetadata()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns ProjectFileCategoryType.values().toSet()
        every { filePersistence.listFileMetadata(PROJECT_ID, any(), Pageable.unpaged()) } returns PageImpl(mutableListOf(
            fileMetadata(time).copy(id = 51L),
            fileMetadata(time).copy(id = 52L),
            fileMetadata(time).copy(id = 53L),
            fileMetadata(time).copy(id = 54L),
            fileMetadata(time).copy(id = 55L),
            fileMetadata(time).copy(id = 56L),
        ))
        every { filePersistence.getCategoriesMap(setOf(51L, 52L, 53L, 54L, 55L, 56L)) } returns mapOf(
            51L to ProjectFileCategoryType.values().toSet(),
            52L to setOf(APPLICATION, INVESTMENT),
            53L to setOf(APPLICATION, PARTNER),
            54L to setOf(APPLICATION),
            55L to setOf(ASSESSMENT),
            56L to setOf(MODIFICATION),
        )

        assertThat(listProjectFileMetadata.list(PROJECT_ID, ProjectFileCategory(APPLICATION, 0L), Pageable.unpaged()).content).containsExactly(
            fileMetadata.copy(id = 51L, uploadedAt = time, category = PARTNER),
            fileMetadata.copy(id = 52L, uploadedAt = time, category = INVESTMENT),
            fileMetadata.copy(id = 53L, uploadedAt = time, category = PARTNER),
            fileMetadata.copy(id = 54L, uploadedAt = time, category = APPLICATION),
            fileMetadata.copy(id = 55L, uploadedAt = time, category = ASSESSMENT),
            fileMetadata.copy(id = 56L, uploadedAt = time, category = MODIFICATION),
        )
    }

}
