package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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

    private val account = UserEntity(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRoleEntity(id = 1, name = "ADMIN"),
        password = "hash_pass",
        userStatus = UserStatus.ACTIVE
    )

    private val call = CallEntity(
        id = 1,
        creator = account,
        name = "Test call name",
        prioritySpecificObjectives = mutableSetOf(),
        strategies = mutableSetOf(),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now(),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(5L),
        status = CallStatus.DRAFT,
        type = CallType.STANDARD,
        translatedValues = mutableSetOf(),
        lengthOfPeriod = 1,
        allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null,
        reportPartnerCheckPluginKey = "check-off",
        projectDefinedUnitCostAllowed = true,
        projectDefinedLumpSumAllowed = false,
    ).apply { translatedValues.add(CallTranslEntity(TranslationId(this, SystemLanguage.EN), "This is a dummy call")) }

    private val statusDraft = ProjectStatusHistoryEntity(
        id = 10,
        status = ApplicationStatus.DRAFT,
        user = account,
        updated = TEST_DATE_TIME
    )

    private val project = ProjectEntity(
        id = 1,
        call = call,
        acronym = "test",
        applicant = account,
        currentStatus = statusDraft,
    )
    private val mockWorkPackage = WorkPackageEntity(
        1, project, 1, false, mutableSetOf()
    ).apply {
        translatedValues.add(WorkPackageTransl(TranslationId(this, SystemLanguage.EN), "Test"))
    }

    private val translatedNameInModel = InputTranslation(SystemLanguage.EN, "Test")
    private val translatedSpecificObjectiveInModel = InputTranslation(SystemLanguage.EN, "Specific Objective")


    private val mockWorkPackageToCreate = InputWorkPackageCreate(
        setOf(translatedNameInModel)
    )

    private val mockWorkPackageToUpdate = InputWorkPackageUpdate(
        1,
        setOf(translatedNameInModel),
        setOf(translatedSpecificObjectiveInModel),
        setOf(),
        false
    )

    private val projectEntityToDeactivate = ProjectEntity(
        id = 1L,
        call = call,
        acronym = "test project",
        applicant = mockk(),
        currentStatus = mockk()
    )

    private val activity = WorkPackageActivityEntity(
        id = 25L,
        workPackage = mockWorkPackage,
        activityNumber = 1,
        deactivated = false
    )

    private val output = WorkPackageOutputEntity(
        WorkPackageOutputId(
            1L, 1
        ),
        periodNumber = 18,
        programmeOutputIndicatorEntity = mockk(),
        deactivated = false
    )

    private val investment = WorkPackageInvestmentEntity(
        id = 55L,
        workPackage = mockWorkPackage,
        investmentNumber = 1,
        address = mockk(),
        deactivated = false
    )

    @MockK
    lateinit var workPackageRepository: WorkPackageRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var workPackageActivityRepository: WorkPackageActivityRepository

    @MockK
    lateinit var workPackageOutputRepository: WorkPackageOutputRepository

    @MockK
    lateinit var workPackageInvestmentRepository: WorkPackageInvestmentRepository

    @RelaxedMockK
    lateinit var projectCollaboratorRepository: UserProjectCollaboratorRepository

    @RelaxedMockK
    lateinit var partnerCollaboratorRepository: UserPartnerCollaboratorRepository

    lateinit var workPackageService: WorkPackageService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        workPackageService = WorkPackageServiceImpl(
            workPackageRepository,
            projectRepository,
            projectCollaboratorRepository,
            partnerCollaboratorRepository,
            workPackageActivityRepository,
            workPackageOutputRepository,
            workPackageInvestmentRepository
        )
    }

    @Test
    fun `createWorkPackage - valid`() {
        every { projectRepository.findById(1L) } returns Optional.of(project)
        every { workPackageRepository.countAllByProjectId(1L) } returns 7
        every { workPackageRepository.save(any()) } returns WorkPackageEntity(
            2, project, 2, false, mutableSetOf()
        ).apply {
            translatedValues.add(WorkPackageTransl(TranslationId(this, SystemLanguage.EN), "Test"))
        }

        val result = workPackageService.createWorkPackage(1, mockWorkPackageToCreate)

        assertThat(result).isNotNull
        assertThat(result.number).isEqualTo(2)
    }

    @Test
    fun `createWorkPackage - max amount reached`() {
        every { projectRepository.findById(1L) } returns Optional.of(project)
        every { workPackageRepository.countAllByProjectId(1L) } returns 20
        val ex = assertThrows<I18nValidationException> {
            workPackageService.createWorkPackage(1, mockWorkPackageToCreate)
        }

        assertThat(ex.i18nKey).isEqualTo("project.workPackage.max.allowed.reached")
    }

    @Test
    fun updateWorkPackage() {
        val workPackageUpdated = WorkPackageEntity(
            1, project, 1, false, mutableSetOf()
        ).apply {
            translatedValues.add(
                WorkPackageTransl(TranslationId(this, SystemLanguage.EN), "Test", "Specific Objective")
            )
        }

        every { workPackageRepository.findById(1L) } returns Optional.of(workPackageUpdated)
        every { workPackageRepository.save(any()) } returnsArgument 0

        val expectedData = OutputWorkPackage(
            id = 1,
            number = 1,
            name = setOf(InputTranslation(SystemLanguage.EN, "Test")),
            specificObjective = setOf(InputTranslation(SystemLanguage.EN, "Specific Objective")),
            objectiveAndAudience = setOf(),
            deactivated = false
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

        assertDoesNotThrow { workPackageService.deleteWorkPackage(1L, mockWorkPackage.id) }
    }

    @Test
    fun deleteWorkPackage_notExisting() {
        every { workPackageRepository.findById(1) } returns Optional.of(mockWorkPackage)
        every { workPackageRepository.deleteById(1) } returns Unit
        every { workPackageRepository.findAllByProjectId(project.id, any()) } returns emptySet()
        every { workPackageRepository.saveAll(emptyList()) } returns emptySet()

        assertDoesNotThrow { workPackageService.deleteWorkPackage(1L, 1) }
    }

    @Test
    fun `getting project for work package should fetch collaborators`() {
        every { workPackageRepository.findById(1) } returns Optional.of(mockWorkPackage)

        workPackageService.getProjectForWorkPackageId(mockWorkPackage.id)

        verify { projectRepository.findById(project.id) }
        verify { partnerCollaboratorRepository.findAllByProjectId(project.id) }
        verify { projectCollaboratorRepository.findAllByIdProjectId(project.id) }
    }

    @Test
    fun `deactivate work package`() {
        every { workPackageRepository.findById(1L) } returns Optional.of(mockWorkPackage)
        every { workPackageRepository.save(any()) } returnsArgument 0
        every { projectRepository.findById(1L) } returns Optional.of(projectEntityToDeactivate)
        every { projectEntityToDeactivate.currentStatus.status } returns ApplicationStatus.CONTRACTED
        every { workPackageActivityRepository.findAllByWorkPackageId(1L) } returns listOf(activity)
        every { workPackageOutputRepository.findAllByOutputIdWorkPackageIdIn(setOf(1L)) } returns listOf(output)
        every { workPackageInvestmentRepository.findAllByWorkPackageId(1L) } returns listOf(investment)

        val result = workPackageService.deactivateWorkPackage(1L, 1L)

        assertThat(result).isNotNull
        assertThat(result.deactivated).isEqualTo(true)
        assertThat(activity.deactivated).isEqualTo(true)
        assertThat(output.deactivated).isEqualTo(true)
        assertThat(investment.deactivated).isEqualTo(true)
    }

    @Test
    fun `try to deactivate when project is not contracted yet`() {
        every { projectRepository.findById(1L) } returns Optional.of(project)
        assertThrows<WorkPackageDeactivationNotAllowedException> { workPackageService.deactivateWorkPackage(1L, 1L) }
    }

}
