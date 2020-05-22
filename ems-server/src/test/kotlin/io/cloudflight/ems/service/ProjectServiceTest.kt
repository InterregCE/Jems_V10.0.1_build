package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.exception.DataValidationException
import io.cloudflight.ems.repository.ProjectRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

val TEST_DATE: LocalDate = LocalDate.now()

class ProjectServiceTest {

    @MockK
    lateinit var projectRepository: ProjectRepository

    lateinit var projectService: ProjectService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectService = ProjectServiceImpl(projectRepository)
    }

    @Test
    fun projectCreation_OK() {
        val project = Project(null, "test", TEST_DATE)
        every { projectRepository.save(eq(project)) } returns Project(1, "test", TEST_DATE)

        val result = projectService.createProject(InputProject("test", TEST_DATE))

        assertEquals(result.acronym, "test")
        assertEquals(result.submissionDate, TEST_DATE)
    }

    @Test
    fun projectCreation_empty() {
        val e = assertThrows<DataValidationException> {
            projectService.createProject(
                InputProject(
                    "",
                    null))
        }
        assertEquals(e.errors.size, 2)
        assertEquals(e.errors["acronym"], listOf("missing"))
        assertEquals(e.errors["submissionDate"], listOf("missing"))
    }

    @Test
    fun projectCreation_invalid() {
        val yesterday = LocalDate.now().minusDays(1)
        val e = assertThrows<DataValidationException> {
            projectService.createProject(
                InputProject(
                    "very long acronym, which is longer than allowed",
                    yesterday))
        }
        assertEquals(e.errors.size, 2)
        assertEquals(e.errors["acronym"], listOf("long"))
        assertEquals(e.errors["submissionDate"], listOf("date_in_past"))
    }

}
