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
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.states.ProjectStatusTestUtil.Companion.userSummary
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

internal class ProjectWorkflowPersistenceTest : UnitTest() {

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

        private fun dummyProject(status: ApplicationStatus = ApplicationStatus.SUBMITTED): ProjectEntity {
            val call = dummyCall()
            val project = ProjectEntity(
                id = PROJECT_ID,
                call = dummyCall(),
                acronym = "Test Project",
                applicant = call.creator,
                currentStatus = ProjectStatusHistoryEntity(
                    id = 1,
                    status = status,
                    user = call.creator
                ),
            );
            val firstStepDecision = ProjectAssessmentEntity(
                eligibilityDecision = ProjectStatusHistoryEntity(
                    id = 2,
                    status = ApplicationStatus.STEP1_ELIGIBLE,
                    user = call.creator,
                    decisionDate = endDate.toLocalDate()
                ),
                fundingDecision = ProjectStatusHistoryEntity(
                    id = 3,
                    status = ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS,
                    user = call.creator,
                    decisionDate = endDate.toLocalDate()
                ),
            );
            val secondStepDecision = ProjectAssessmentEntity(
                eligibilityDecision = ProjectStatusHistoryEntity(
                    id = 2,
                    status = ApplicationStatus.ELIGIBLE,
                    user = call.creator,
                    decisionDate = endDate.toLocalDate()
                ),
                fundingDecision = ProjectStatusHistoryEntity(
                    id = 3,
                    status = ApplicationStatus.APPROVED_WITH_CONDITIONS,
                    user = call.creator,
                    decisionDate = endDate.toLocalDate()
                ),
            )
            project.decisionEligibilityStep1 = firstStepDecision.eligibilityDecision
            project.decisionFundingStep1 = firstStepDecision.fundingDecision
            project.decisionEligibilityStep2 = secondStepDecision.eligibilityDecision
            project.decisionFundingStep2 = secondStepDecision.fundingDecision
            return project
        }
    }

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var projectStatusHistoryRepository: ProjectStatusHistoryRepository

    @InjectMockKs
    private lateinit var persistence: ProjectWorkflowPersistenceProvider

    @Test
    fun `get Project EligibilityDecisionDate - not existing`() {
        every { projectRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectEligibilityDecisionDate(-1) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `get Project EligibilityDecisionDate - everything OK - step 2`() {
        val project = dummyProject(status = ApplicationStatus.SUBMITTED)
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectEligibilityDecisionDate(PROJECT_ID)).isEqualTo(
            project.decisionEligibilityStep2?.decisionDate
        )
    }

    @Test
    fun `get Project EligibilityDecisionDate - everything OK - step 2 NULL`() {
        val project = dummyProject(status = ApplicationStatus.SUBMITTED).copy(decisionEligibilityStep2 = null)
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectEligibilityDecisionDate(PROJECT_ID)).isNull()
    }

    @Test
    fun `get Project EligibilityDecisionDate - everything OK - step 1`() {
        val project = dummyProject(status = ApplicationStatus.STEP1_SUBMITTED).copy(
            decisionEligibilityStep2 = null,
        )
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectEligibilityDecisionDate(PROJECT_ID)).isEqualTo(
            project.decisionEligibilityStep1?.decisionDate
        )
    }

    @Test
    fun `get Project EligibilityDecisionDate - everything OK - step 1 NULL`() {
        val project = dummyProject(status = ApplicationStatus.STEP1_SUBMITTED).copy(
            decisionEligibilityStep1 = null,
            decisionEligibilityStep2 = null,
        )
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectEligibilityDecisionDate(PROJECT_ID)).isNull()
    }

    @Test
    fun `get Project previous Status`() {
        val statusHistories = listOf(
            ProjectStatusHistoryEntity(
                id = 1,
                status = ApplicationStatus.DRAFT,
                user = ProjectPartnerTestUtil.user,
                updated = startDate
            ),
            ProjectStatusHistoryEntity(
                id = 2,
                status = ApplicationStatus.INELIGIBLE,
                user = ProjectPartnerTestUtil.user,
                updated = endDate,
                decisionDate = LocalDate.of(2021, 5, 19),
                note = "explanatory note"
            )
        )
        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(PROJECT_ID) } returns statusHistories
        assertThat(persistence.getApplicationPreviousStatus(PROJECT_ID)).isEqualTo(
            ProjectStatus(
                id = 2,
                status = ApplicationStatus.INELIGIBLE,
                user = ProjectPartnerTestUtil.userSummary,
                updated = endDate,
                decisionDate = LocalDate.of(2021, 5, 19),
                note = "explanatory note",
            )
        )
    }

    @Test
    fun `get Project previous Status - Error`() {
        val statusHistories = listOf(
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = ProjectPartnerTestUtil.user)
        )
        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(PROJECT_ID) } returns statusHistories
        assertThrows<PreviousApplicationStatusNotFoundException> { persistence.getApplicationPreviousStatus(PROJECT_ID) }
    }

    @Test
    fun `get latest Application Status NotEqual`() {
        val status =
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.SUBMITTED, user = ProjectPartnerTestUtil.user)
        every {
            projectStatusHistoryRepository.findFirstByProjectIdAndStatusNotOrderByUpdatedDesc(
                PROJECT_ID,
                ApplicationStatus.APPROVED
            )
        } returns status
        assertThat(persistence.getLatestApplicationStatusNotEqualTo(PROJECT_ID, ApplicationStatus.APPROVED))
            .isEqualTo(ApplicationStatus.SUBMITTED)
    }

    @Test
    fun `update Application first Submission`() {
        val user = ProjectPartnerTestUtil.user
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        every { userRepository.getOne(user.id) } returns user
        val status =
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.SUBMITTED, user = user, updated = startDate)
        // any because of auto set updated timestamp
        every { projectStatusHistoryRepository.save(any()) } returns status

        assertThat(persistence.updateApplicationFirstSubmission(PROJECT_ID, user.id, ApplicationStatus.SUBMITTED))
            .isEqualTo(ApplicationStatus.SUBMITTED)
    }

    @Test
    fun `update Application last ReSubmission`() {
        val user = ProjectPartnerTestUtil.user
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        every { userRepository.getOne(user.id) } returns user
        val status =
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.APPROVED, user = user, updated = endDate)
        // any because of auto set updated timestamp
        val slot = slot<ProjectStatusHistoryEntity>()
        every { projectStatusHistoryRepository.save(capture(slot)) } returns status

        val toClone = ProjectStatus(
            status = ApplicationStatus.APPROVED,
            user = userSummary,
            updated = ZonedDateTime.now(),
            decisionDate = LocalDate.of(2021, 5, 19),
            note = "note to be persisted",
        )

        assertThat(persistence.updateProjectLastResubmission(PROJECT_ID, user.id, toClone))
            .isEqualTo(ApplicationStatus.APPROVED)
        with(slot.captured) {
            assertThat(this.status).isEqualTo(ApplicationStatus.APPROVED)
            assertThat(decisionDate).isEqualTo(LocalDate.of(2021, 5, 19))
            assertThat(note).isEqualTo("note to be persisted")
        }
    }

    @Test
    fun `update Project current Status`() {
        val user = ProjectPartnerTestUtil.user
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        every { userRepository.getOne(user.id) } returns user
        val status =
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.SUBMITTED, user = user, updated = endDate)
        // any because of auto set updated timestamp
        every { projectStatusHistoryRepository.save(any()) } returns status

        assertThat(
            persistence.updateProjectCurrentStatus(
                PROJECT_ID,
                user.id,
                ApplicationStatus.SUBMITTED,
                ApplicationActionInfo("note", null)
            )
        )
            .isEqualTo(ApplicationStatus.SUBMITTED)
    }

    @Test
    fun `revert current Project Status`() {
        val user = ProjectPartnerTestUtil.user
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        val statusHistories = listOf(
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = user, updated = startDate),
            ProjectStatusHistoryEntity(id = 2, status = ApplicationStatus.SUBMITTED, user = user, updated = endDate)
        )
        every { projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(PROJECT_ID) } returns statusHistories
        every { projectStatusHistoryRepository.delete(project.currentStatus) } returns Unit
        // any because of auto set updated timestamp
        every { projectStatusHistoryRepository.save(any()) } returns statusHistories[1]

        assertThat(persistence.revertCurrentStatusToPreviousStatus(PROJECT_ID))
            .isEqualTo(ApplicationStatus.SUBMITTED)
    }

    @Test
    fun `reset Project FundingDecision`() {
        every { projectRepository.getOne(PROJECT_ID) } returns dummyProject()
        assertThat(persistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID))
            .isEqualTo(ApplicationStatus.SUBMITTED)
    }

    @Test
    fun `update Project EligibilityDecision`() {
        val user = ProjectPartnerTestUtil.user
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        every { userRepository.getOne(user.id) } returns user
        val statusHistories = listOf(
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = user, updated = startDate),
            ProjectStatusHistoryEntity(id = 2, status = ApplicationStatus.APPROVED, user = user, updated = endDate)
        )
        // any because of auto set updated timestamp
        every { projectStatusHistoryRepository.save(any()) } returns statusHistories[1]

        assertThat(
            persistence.updateProjectEligibilityDecision(
                PROJECT_ID,
                user.id,
                ApplicationStatus.APPROVED,
                ApplicationActionInfo("note", null)
            )
        )
            .isEqualTo(ApplicationStatus.APPROVED)
    }

    @Test
    fun `update Project FundingDecision`() {
        val user = ProjectPartnerTestUtil.user
        val project = dummyProject()
        every { projectRepository.getOne(PROJECT_ID) } returns project
        every { userRepository.getOne(user.id) } returns user
        val statusHistories = listOf(
            ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = user, updated = startDate),
            ProjectStatusHistoryEntity(id = 2, status = ApplicationStatus.APPROVED, user = user, updated = endDate)
        )
        // any because of auto set updated timestamp
        every { projectStatusHistoryRepository.save(any()) } returns statusHistories[1]

        assertThat(
            persistence.updateProjectFundingDecision(
                PROJECT_ID,
                user.id,
                ApplicationStatus.APPROVED,
                ApplicationActionInfo("note", null)
            )
        )
            .isEqualTo(ApplicationStatus.APPROVED)
    }

}
