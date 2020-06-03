package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.exception.DataValidationException
import io.cloudflight.ems.repository.ProjectRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.Optional

val TEST_DATE: LocalDate = LocalDate.now()

class ProjectServiceTest {

    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var auditService: AuditService

    lateinit var projectService: ProjectService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectService = ProjectServiceImpl(projectRepository, auditService)
        every { auditService.logEvent(any()) } answers {} // doNothing
    }

    @Test
    fun projectCreation_OK() {
        val project = Project(null, "test", TEST_DATE)
        every { projectRepository.save(eq(project)) } returns Project(612, "test", TEST_DATE)

        val result = projectService.createProject(InputProject("test", TEST_DATE))

        assertEquals(result.acronym, "test")
        assertEquals(result.submissionDate, TEST_DATE)

        verifyAudit("612")
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
        assertEquals(e.errors.size, 1)
        assertEquals(e.errors["acronym"], listOf("long"))
    }

    @Test
    fun projectGet_OK() {
        every { projectRepository.findById(eq(1)) } returns Optional.of(Project(1, "test", TEST_DATE))

        val result = projectService.getProjectById(1);
        assertTrue(result.isPresent)
        with (result.get()) {
            assertEquals(1, id)
            assertEquals("test", acronym)
            assertEquals(TEST_DATE, submissionDate)
        }
    }

    @Test
    fun projectGet_invalid() {
        every { projectRepository.findById(eq(2)) } returns Optional.empty()

        val result = projectService.getProjectById(2);
        assertFalse(result.isPresent)
    }

    private fun verifyAudit(projectIdExpected: String) {
        val event = slot<Audit>()

        verify { auditService.logEvent(capture(event)) }
        with(event.captured) {
            assertEquals(projectIdExpected, projectId)
            assertEquals(AuditAction.PROJECT_SUBMISSION, action)
        }
    }

}
