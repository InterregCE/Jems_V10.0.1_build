package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
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
import io.cloudflight.jems.server.project.entity.ProjectRow
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.toProjectStatus
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
                step2Active = false
            )
        }
    }

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository
    @RelaxedMockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    private lateinit var persistence: ProjectPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPersistenceProvider(projectVersionUtils, projectRepository, projectPartnerRepository)
    }

    @Test
    fun `getProjectSummary - everything OK`() {
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        assertThat(persistence.getProjectSummary(PROJECT_ID)).isEqualTo(
            ProjectSummary(
                id = PROJECT_ID,
                callName = "call name",
                acronym = project.acronym,
                status = project.currentStatus.status,
            )
        )
    }

    @Test
    fun `get Project Call Settings`() {
        val project = dummyProject()
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectCallSettings(PROJECT_ID)).isEqualTo(
            project.call.toSettingsModel()
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
    fun `get ProjectId for Partner`() {
        every { projectPartnerRepository.getProjectIdForPartner(1) } returns PROJECT_ID
        assertThat(persistence.getProjectIdForPartner(PROJECT_ID)).isEqualTo(PROJECT_ID)
    }

    @Test
    fun `get ProjectId for Partner - not existing`() {
        every { projectPartnerRepository.getProjectIdForPartner(1) } returns null
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectIdForPartner(1) }
        assertThat(ex.entity).isEqualTo("ProjectPartner")
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
        val project = dummyProject()
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)

        assertThat(persistence.getProject(PROJECT_ID))
            .isEqualTo(
                Project(
                    id = project.id,
                    intro = null,
                    title = null,
                    acronym = project.acronym,
                    duration = project.projectData?.duration,
                    step2Active = project.step2Active,
                    periods = listOf(ProjectPeriod(1, 1, 2)),
                    applicant = project.applicant.toUserSummary(),
                    projectStatus = project.currentStatus.toProjectStatus(),
                    firstSubmission = project.firstSubmission?.toProjectStatus(),
                    lastResubmission = project.lastResubmission?.toProjectStatus(),
                    callSettings = project.call.toSettingsModel(),
                    programmePriority = project.priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
                    specificObjective = project.priorityPolicy?.toOutputProgrammePriorityPolicy()
                ))
    }

    @Test
    fun `get Project with previous version`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val project = dummyProject()
        val version = 3
        val mockRow: ProjectRow = mockk()
        every { mockRow.id } returns 1L
        every { mockRow.language } returns SystemLanguage.EN
        every { mockRow.intro } returns "intro"
        every { mockRow.title } returns "title"
        every { mockRow.acronym } returns "acronym"
        every { mockRow.duration } returns 12
        every { mockRow.step2Active } returns false
        every { mockRow.periodNumber } returns 1
        every { mockRow.periodStart } returns 1
        every { mockRow.periodEnd } returns 12
        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)

        every { projectRepository.findByIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockRow)
        assertThat(persistence.getProject(PROJECT_ID, version))
            .isEqualTo(
                Project(
                    id = mockRow.id,
                    intro = setOf(InputTranslation(mockRow.language!!, mockRow.intro)),
                    title = setOf(InputTranslation(mockRow.language!!, mockRow.title)),
                    acronym = mockRow.acronym,
                    duration = mockRow.duration,
                    step2Active = mockRow.step2Active,
                    periods = listOf(ProjectPeriod(mockRow.periodNumber!!, mockRow.periodStart!!, mockRow.periodEnd!!)),
                    applicant = project.applicant.toUserSummary(),
                    projectStatus = project.currentStatus.toProjectStatus(),
                    firstSubmission = project.firstSubmission?.toProjectStatus(),
                    lastResubmission = project.lastResubmission?.toProjectStatus(),
                    callSettings = project.call.toSettingsModel(),
                    programmePriority = project.priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple(),
                    specificObjective = project.priorityPolicy?.toOutputProgrammePriorityPolicy()
                ))
    }
}
