package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.repository.CallPersistenceProvider
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.costoption.combineUnitCostTranslatedValues
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectPeriodRow
import io.cloudflight.jems.server.project.entity.ProjectRow
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEligibilityEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentQualityEntity
import io.cloudflight.jems.server.project.repository.assessment.ProjectAssessmentEligibilityRepository
import io.cloudflight.jems.server.project.repository.assessment.ProjectAssessmentQualityRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Optional

/**
 * tests implementation of ProjectPersistenceProvider including mappings and projectVersionUtils
 */
internal class ProjectPersistenceTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val CALL_ID = 12L

        val startDate: ZonedDateTime = ZonedDateTime.now().minusDays(2)
        val endDate: ZonedDateTime = ZonedDateTime.now().plusDays(2)

        private fun dummyCall(): CallEntity {
            val call = callWithId(CALL_ID)
            call.name = "call name"
            call.startDate = startDate
            call.endDate = endDate
            call.lengthOfPeriod = 9
            call.flatRates.clear()
            call.flatRates.add(
                ProjectCallFlatRateEntity(
                    setupId = FlatRateSetupId(call, FlatRateType.STAFF_COSTS),
                    rate = 15,
                    isAdjustable = true
                )
            )
            call.lumpSums.clear()
            call.lumpSums.add(
                ProgrammeLumpSumEntity(
                    id = 32,
                    translatedValues = combineLumpSumTranslatedValues(
                        programmeLumpSumId = 32,
                        name = setOf(InputTranslation(SystemLanguage.EN, "LumpSum")),
                        description = setOf(InputTranslation(SystemLanguage.EN, "pls 32"))
                    ),
                    cost = BigDecimal.TEN,
                    splittingAllowed = false,
                    phase = ProgrammeLumpSumPhase.Preparation,
                    categories = mutableSetOf(
                        ProgrammeLumpSumBudgetCategoryEntity(
                            programmeLumpSumId = 12,
                            category = BudgetCategory.EquipmentCosts
                        ),
                        ProgrammeLumpSumBudgetCategoryEntity(
                            programmeLumpSumId = 13,
                            category = BudgetCategory.TravelAndAccommodationCosts
                        ),
                    ),
                )
            )
            call.unitCosts.clear()
            call.unitCosts.add(
                ProgrammeUnitCostEntity(
                    id = 4,
                    translatedValues = combineUnitCostTranslatedValues(
                        programmeUnitCostId = 32,
                        name = setOf(InputTranslation(SystemLanguage.EN, "UnitCost")),
                        description = setOf(InputTranslation(SystemLanguage.EN, "plus 4")),
                        type = setOf(InputTranslation(SystemLanguage.EN, "type of unit cost"))
                    ),
                    costPerUnit = BigDecimal.ONE,
                    isOneCostCategory = false,
                    categories = mutableSetOf(
                        ProgrammeUnitCostBudgetCategoryEntity(
                            programmeUnitCostId = 14,
                            category = BudgetCategory.ExternalCosts
                        ),
                        ProgrammeUnitCostBudgetCategoryEntity(
                            programmeUnitCostId = 15,
                            category = BudgetCategory.OfficeAndAdministrationCosts
                        ),
                    ),
                )
            )
            return call
        }

        private fun dummyProject(): ProjectEntity {
            val call = dummyCall()
            return ProjectEntity(
                id = PROJECT_ID,
                call = dummyCall(),
                acronym = "Test Project",
                applicant = call.creator,
                currentStatus = ProjectStatusHistoryEntity(
                    id = 1,
                    status = ApplicationStatus.DRAFT,
                    user = call.creator
                ),
                periods = listOf(
                    ProjectPeriodEntity(
                        id = ProjectPeriodId(projectId = PROJECT_ID, number = 1),
                        start = 1,
                        end = 2,
                    )
                ),
            )
        }
    }
    private val applicationFormConfiguration = ApplicationFormConfiguration(1,"test configuration", mutableSetOf())

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository
    @MockK
    lateinit var projectAssessmentQualityRepository: ProjectAssessmentQualityRepository
    @MockK
    lateinit var projectAssessmentEligibilityRepository: ProjectAssessmentEligibilityRepository
    @MockK
    lateinit var projectStatusHistoryRepo: ProjectStatusHistoryRepository
    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var callRepository: CallRepository
    @MockK
    lateinit var callPersistence: CallPersistenceProvider

    private lateinit var persistence: ProjectPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPersistenceProvider(projectVersionUtils, projectRepository, projectPartnerRepository, projectAssessmentQualityRepository, projectAssessmentEligibilityRepository, projectStatusHistoryRepo, userRepository, callRepository, callPersistence)
        every { callPersistence.getApplicationFormConfiguration(1) } returns ApplicationFormConfiguration(1,"test configuration", mutableSetOf())
    }

    @Test
    fun `getProjectSummary - everything OK`() {
        val statusChange = ZonedDateTime.now()
        val project = dummyProject().copy(
            firstSubmission = ProjectStatusHistoryEntity(
                id = 669L,
                status = ApplicationStatus.SUBMITTED,
                user = dummyCall().creator,
                updated = statusChange,
            ),
            lastResubmission = ProjectStatusHistoryEntity(
                id = 670L,
                status = ApplicationStatus.SUBMITTED,
                user = dummyCall().creator,
                updated = statusChange,
            ),
            priorityPolicy = ProgrammeSpecificObjectiveEntity(
                programmeObjectivePolicy = ProgrammeObjectivePolicy.CrossBorderMobility,
                code = "SO2.4",
                programmePriority = ProgrammePriorityEntity(
                    id = 589L,
                    code = "SO2",
                    objective = ProgrammeObjectivePolicy.CrossBorderMobility.objective
                ),
            ),
        )
        every { projectRepository.getOne(PROJECT_ID) } returns project
        assertThat(persistence.getProjectSummary(PROJECT_ID)).isEqualTo(
            ProjectSummary(
                id = PROJECT_ID,
                callName = "call name",
                acronym = project.acronym,
                status = project.currentStatus.status,
                firstSubmissionDate = statusChange,
                lastResubmissionDate = statusChange,
                specificObjectiveCode = "SO2.4",
                programmePriorityCode = "SO2",
            )
        )
    }

    @Test
    fun `get Project Call Settings`() {
        val project = dummyProject()
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectCallSettings(PROJECT_ID)).isEqualTo(
            project.call.toSettingsModel(applicationFormConfiguration)
        )
    }

    @Test
    fun `get Project UnitCosts`() {
        val project = dummyProject()
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectUnitCosts(PROJECT_ID)).isEqualTo(
            project.call.unitCosts.toModel()
        )
    }

    @Test
    fun `get Project Periods`() {
        val project = dummyProject()
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectPeriods(PROJECT_ID)).isEqualTo(
            project.periods.toProjectPeriods()
        )
    }

    @Test
    fun `get Project without version`() {
        val statusChange = ZonedDateTime.now()
        val user = dummyCall().creator
        val project = dummyProject().copy(
            decisionFundingStep1 = ProjectStatusHistoryEntity(
                id = 896L,
                status = ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS,
                user = user,
                updated = statusChange,
            ),
            decisionEligibilityStep2 = ProjectStatusHistoryEntity(
                id = 897L,
                status = ApplicationStatus.ELIGIBLE,
                user = user,
                updated = statusChange,
            ),
        )
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        every { projectAssessmentQualityRepository.findById(ProjectAssessmentId(project, 1)) } returns Optional.of(
            ProjectAssessmentQualityEntity(ProjectAssessmentId(project, 1), result = ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING, user = user, updated = statusChange)
        )
        every { projectAssessmentQualityRepository.findById(ProjectAssessmentId(project, 2)) } returns Optional.empty()

        every { projectAssessmentEligibilityRepository.findById(ProjectAssessmentId(project, 1)) } returns Optional.empty()
        every { projectAssessmentEligibilityRepository.findById(ProjectAssessmentId(project, 2)) } returns Optional.of(
            ProjectAssessmentEligibilityEntity(ProjectAssessmentId(project, 1), result = ProjectAssessmentEligibilityResult.PASSED, user = user, updated = statusChange)
        )

        assertThat(persistence.getProject(PROJECT_ID))
            .isEqualTo(
                Project(
                    id = project.id,
                    intro = null,
                    title = null,
                    acronym = project.acronym,
                    duration = project.projectData?.duration,
                    periods = listOf(ProjectPeriod(1, 1, 2)),
                    applicant = project.applicant.toUserSummary(),
                    projectStatus = project.currentStatus.toProjectStatus(),
                    firstSubmission = project.firstSubmission?.toProjectStatus(),
                    lastResubmission = project.lastResubmission?.toProjectStatus(),
                    callSettings = project.call.toSettingsModel(applicationFormConfiguration),
                    programmePriority = project.priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
                    specificObjective = project.priorityPolicy?.toOutputProgrammePriorityPolicy(),
                    assessmentStep1 = ProjectAssessment(
                        assessmentQuality = ProjectAssessmentQuality(
                            projectId = project.id,
                            step = 1,
                            result = ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING,
                            updated = statusChange,
                        ),
                        fundingDecision = ProjectStatus(
                            id = 896L,
                            status = ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS,
                            user = UserSummary(id = user.id, email = user.email, name = user.name, surname = user.surname, userRole = UserRoleSummary(id = 1, name = "ADMIN")),
                            updated = statusChange,
                        )
                    ),
                    assessmentStep2 = ProjectAssessment(
                        assessmentEligibility = ProjectAssessmentEligibility(
                            projectId = project.id,
                            step = 1,
                            result = ProjectAssessmentEligibilityResult.PASSED,
                            updated = statusChange,
                        ),
                        eligibilityDecision = ProjectStatus(
                            id = 897L,
                            status = ApplicationStatus.ELIGIBLE,
                            user = UserSummary(id = user.id, email = user.email, name = user.name, surname = user.surname, userRole = UserRoleSummary(id = 1, name = "ADMIN")),
                            updated = statusChange,
                        )
                    ),
                )
            )
    }

    @Test
    fun `get Project with previous version`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val project = dummyProject()
        val version = "3.0"
        val mockRow: ProjectRow = mockk()
        val mockPeriodRow: ProjectPeriodRow = mockk()
        every { mockRow.id } returns 1L
        every { mockRow.language } returns SystemLanguage.EN
        every { mockRow.acronym } returns "acronym"
        every { mockRow.duration } returns 12
        every { mockRow.title } returns "title"
        every { mockRow.intro } returns "intro"
        every { mockPeriodRow.periodNumber } returns 1
        every { mockPeriodRow.periodStart } returns 1
        every { mockPeriodRow.periodEnd } returns 12

        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp

        every { projectAssessmentQualityRepository.findById(any()) } returns Optional.empty()
        every { projectAssessmentEligibilityRepository.findById(any()) } returns Optional.empty()

        every { projectRepository.findPeriodsByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockPeriodRow)
        every { projectRepository.findByIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockRow)

        assertThat(persistence.getProject(PROJECT_ID, version))
            .isEqualTo(
                Project(
                    id = mockRow.id,
                    intro = setOf(InputTranslation(mockRow.language!!, mockRow.intro)),
                    title = setOf(InputTranslation(mockRow.language!!, mockRow.title)),
                    acronym = mockRow.acronym,
                    duration = mockRow.duration,
                    periods = listOf(ProjectPeriod(mockPeriodRow.periodNumber!!, mockPeriodRow.periodStart!!, mockPeriodRow.periodEnd!!)),
                    applicant = project.applicant.toUserSummary(),
                    projectStatus = project.currentStatus.toProjectStatus(),
                    firstSubmission = project.firstSubmission?.toProjectStatus(),
                    lastResubmission = project.lastResubmission?.toProjectStatus(),
                    callSettings = project.call.toSettingsModel(applicationFormConfiguration),
                    programmePriority = project.priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
                    specificObjective = project.priorityPolicy?.toOutputProgrammePriorityPolicy()
                ))
    }

    @Test
    fun `get Project without versions`() {
        val project = dummyProject()
        val notExistingVersion = "3.0"

        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, notExistingVersion) } returns null

        every { projectAssessmentQualityRepository.findById(any()) } returns Optional.empty()
        every { projectAssessmentEligibilityRepository.findById(any()) } returns Optional.empty()

        assertThrows<ApplicationVersionNotFoundException> { persistence.getProject(PROJECT_ID, notExistingVersion) }
    }

    @Test
    fun `getProjects - not owner`() {
        every { projectRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(dummyProject()))

        val result = persistence.getProjects(Pageable.unpaged(), null)

        assertThat(result.numberOfElements).isEqualTo(1)
        assertThat(result.elementAt(0)).isEqualTo(
            ProjectSummary(
                id = PROJECT_ID,
                callName = "call name",
                acronym = "Test Project",
                status = ApplicationStatus.DRAFT,
            )
        )
    }

    @Test
    fun `getProjects - owner`() {
        every { projectRepository.findAllByApplicantId(7006L, Pageable.unpaged()) } returns PageImpl(listOf(dummyProject()))

        val result = persistence.getProjects(Pageable.unpaged(), 7006L)

        assertThat(result.numberOfElements).isEqualTo(1)
        assertThat(result.elementAt(0)).isEqualTo(
            ProjectSummary(
                id = PROJECT_ID,
                callName = "call name",
                acronym = "Test Project",
                status = ApplicationStatus.DRAFT,
            )
        )
    }
}
