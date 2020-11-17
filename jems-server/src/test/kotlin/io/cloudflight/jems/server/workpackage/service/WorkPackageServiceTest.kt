package io.cloudflight.jems.server.workpackage.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackage
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.domain.Sort
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkPackageServiceTest {
    private val TEST_DATE: LocalDate = LocalDate.now()
    private val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(10, 0), ZoneId.of("Europe/Bratislava"))

    private val account = User(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN"),
        password = "hash_pass"
    )

    private val user = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    private val call = Call(
        id = 1,
        creator = account,
        name = "Test call name",
        priorityPolicies = emptySet(),
        strategies = emptySet(),
        funds = emptySet(),
        startDate = ZonedDateTime.now(),
        endDate = ZonedDateTime.now().plusDays(5L),
        status = CallStatus.DRAFT,
        description = "This is a dummy call",
        lengthOfPeriod = 1
    )

    private val statusDraft = ProjectStatus(
        id = 10,
        status = ProjectApplicationStatus.DRAFT,
        user = account,
        updated = TEST_DATE_TIME
    )

    private val project = Project(
        id = 1,
        call = call,
        acronym = "test",
        applicant = account,
        projectStatus = statusDraft
    )

    private val mockWorkPackage = WorkPackage(
        1,
        project,
        1,
        "Test",
        "",
        ""
    )

    private val mockWorkPackageToCreate = InputWorkPackageCreate(
        "Test",
        "",
        ""
    )

    private val mockWorkPackageToUpdate = InputWorkPackageUpdate(
        1,
        "Test",
        "Specific Objective",
        ""
    )

    @RelaxedMockK
    lateinit var workPackageRepository: WorkPackageRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    lateinit var workPackageService: WorkPackageService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        workPackageService = WorkPackageServiceImpl(
            workPackageRepository,
            projectRepository
        )
    }

    @Test
    fun getWorkPackageById() {
        every { workPackageRepository.findById(1L) } returns Optional.of(mockWorkPackage)

        val result = workPackageService.getWorkPackageById(1L)

        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo("Test")
    }

    @Test
    fun getWorkPackagesByProjectId() {
        every { workPackageRepository.findById(1L) } returns Optional.of(mockWorkPackage)

        val result = workPackageService.getWorkPackageById(1L)

        assertThat(result).isNotNull
    }

    @Test
    fun createWorkPackage() {
        every { projectRepository.findById(1L) } returns Optional.of(project)
        every { workPackageRepository.save(any<WorkPackage>()) } returns WorkPackage(
            2,
            project,
            2,
            "Test",
            "",
            ""
        )

        val result = workPackageService.createWorkPackage(1, mockWorkPackageToCreate)

        assertThat(result).isNotNull
        assertThat(result.number).isEqualTo(2)
    }

    @Test
    fun updateWorkPackage() {
        val workPackageUpdated = WorkPackage(
            1,
            project,
            1,
            "Test",
            "Specific Objective",
            ""
        )

        every { workPackageRepository.findFirstByProjectIdAndId(1L, 1L) } returns workPackageUpdated
        every { workPackageRepository.save(any<WorkPackage>()) } returnsArgument 0

        val expectedData = OutputWorkPackage (
            id = 1,
            number = 1,
            name = "Test",
            specificObjective = "Specific Objective",
            objectiveAndAudience = ""
        )

        val result = workPackageService.updateWorkPackage(1L, mockWorkPackageToUpdate)

        assertThat(result).isNotNull
        assertThat(result.number).isEqualTo(expectedData.number)
    }

    @Test
    fun deleteWorkPackage() {
        every { workPackageRepository.deleteById(mockWorkPackage.id) } returns Unit
        every { workPackageRepository.findAllByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { workPackageRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { workPackageService.deleteWorkPackage(project.id, mockWorkPackage.id) }
    }

    @Test
    fun deleteWorkPackage_notExisting() {
        every { workPackageRepository.deleteById(100) } returns Unit
        every { workPackageRepository.findAllByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { workPackageRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { workPackageService.deleteWorkPackage(project.id, 100) }
    }

}
