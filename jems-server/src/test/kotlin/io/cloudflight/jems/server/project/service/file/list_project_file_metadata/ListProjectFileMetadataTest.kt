package io.cloudflight.jems.server.project.service.file.list_project_file_metadata

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.authorization.ProjectFileAuthorization
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.utils.fileMetadata
import io.cloudflight.jems.server.utils.projectFileCategory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class ListProjectFileMetadataTest : UnitTest() {

    @MockK
    lateinit var filePersistence: ProjectFilePersistence

    @MockK
    lateinit var authorization: ProjectFileAuthorization

    @InjectMockKs
    lateinit var listProjectFileMetadata: ListProjectFileMetadata

    @Test
    fun `should return list of project files metadata`() {
        val category = projectFileCategory()
        val fileMetadata = fileMetadata()
        every { authorization.getRetrievableCategories(PROJECT_ID) } returns setOf(category.type)
        every {
            filePersistence.listFileMetadata(PROJECT_ID, category, Pageable.unpaged())
        } returns PageImpl(mutableListOf(fileMetadata))

        assertThat(listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged()))
            .containsExactly(fileMetadata)
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
        listProjectFileMetadata.list(PROJECT_ID, category, Pageable.unpaged())
        assertThat(categorySlot.captured).isEqualTo(ProjectFileCategory(ProjectFileCategoryType.ALL, null))
    }
}
