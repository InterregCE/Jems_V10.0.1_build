package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.repository.ProjectRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.Optional
import java.util.stream.Collectors

val TEST_DATE: LocalDate = LocalDate.now()

class ProjectServiceTest {

    private val UNPAGED = Pageable.unpaged()

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
    fun projectRetrieval() {
        val projectToReturn = Project(
            id = 25,
            acronym = "test acronym",
            submissionDate = TEST_DATE
        )
        every { projectRepository.findAll(UNPAGED) } returns PageImpl(listOf(projectToReturn))

        // test start
        val result = projectService.getProjects(UNPAGED)

        // assertions:
        assertEquals(1, result.totalElements)

        val expectedProjects = listOf(
            OutputProject(
                id = 25,
                acronym = "test acronym",
                submissionDate = TEST_DATE
            )
        )
        assertIterableEquals(expectedProjects, result.get().collect(Collectors.toList()))
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
    fun projectGet_OK() {
        every { projectRepository.findById(eq(1)) } returns Optional.of(Project(1, "test", TEST_DATE))

        val result = projectService.getProjectById(1);
        assertTrue(result.isPresent)
        with(result.get()) {
            assertEquals(1, id)
            assertEquals("test", acronym)
            assertEquals(TEST_DATE, submissionDate)
        }
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
