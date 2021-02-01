package io.cloudflight.jems.server.workpackage.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageId
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
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

    private val call = CallEntity(
        id = 1,
        creator = account,
        name = "Test call name",
        prioritySpecificObjectives = emptySet(),
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

    private val project = ProjectEntity(
        id = 1,
        call = call,
        acronym = "test",
        applicant = account,
        projectStatus = statusDraft
    )

    private val translatedNameInEntity = WorkPackageTransl(TranslationWorkPackageId(1, SystemLanguage.EN), "Test")
    private val translatedNameInModel = InputTranslation(SystemLanguage.EN, "Test")
    private val translatedSpecificObjectiveInModel = InputTranslation(SystemLanguage.EN, "Specific Objective")

    private val mockWorkPackage = WorkPackageEntity(
        1,
        project,
        1,
        mutableSetOf(translatedNameInEntity)
    )

    private val mockWorkPackageToCreate = InputWorkPackageCreate(
        setOf(translatedNameInModel)
    )

    private val mockWorkPackageToUpdate = InputWorkPackageUpdate(
        1,
        setOf(translatedNameInModel),
        setOf(translatedSpecificObjectiveInModel),
        setOf()
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
        assertThat(result.name).isEqualTo(setOf(translatedNameInModel))
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
        every { workPackageRepository.save(any<WorkPackageEntity>()) } returns WorkPackageEntity(
            2,
            project,
            2,
            mutableSetOf(WorkPackageTransl(TranslationWorkPackageId(1, SystemLanguage.EN), "Test"))
        )

        val result = workPackageService.createWorkPackage(1, mockWorkPackageToCreate)

        assertThat(result).isNotNull
        assertThat(result.number).isEqualTo(2)
    }

    @Test
    fun updateWorkPackage() {
        val workPackageUpdated = WorkPackageEntity(
            1,
            project,
            1,
            mutableSetOf(WorkPackageTransl(TranslationWorkPackageId(1, SystemLanguage.EN), "Test", "Specific Objective"))
        )

        every { workPackageRepository.findById(1L) } returns Optional.of(workPackageUpdated)
        every { workPackageRepository.save(any<WorkPackageEntity>()) } returnsArgument 0

        val expectedData = OutputWorkPackage (
            id = 1,
            number = 1,
            name = setOf(InputTranslation(SystemLanguage.EN, "Test")),
            specificObjective = setOf(InputTranslation(SystemLanguage.EN, "Specific Objective")),
            objectiveAndAudience = setOf()
        )

        val result = workPackageService.updateWorkPackage(mockWorkPackageToUpdate)

        assertThat(result).isNotNull
        assertThat(result.number).isEqualTo(expectedData.number)
    }

    @Test
    fun deleteWorkPackage() {
        every { workPackageRepository.deleteById(mockWorkPackage.id) } returns Unit
        every { workPackageRepository.findAllByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { workPackageRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { workPackageService.deleteWorkPackage(mockWorkPackage.id) }
    }

    @Test
    fun deleteWorkPackage_notExisting() {
        every { workPackageRepository.findById(100) } returns Optional.of(mockWorkPackage.copy(id = 100))
        every { workPackageRepository.deleteById(100) } returns Unit
        every { workPackageRepository.findAllByProjectId(project.id, any<Sort>()) } returns emptySet()
        every { workPackageRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { workPackageService.deleteWorkPackage(100) }
    }

}
