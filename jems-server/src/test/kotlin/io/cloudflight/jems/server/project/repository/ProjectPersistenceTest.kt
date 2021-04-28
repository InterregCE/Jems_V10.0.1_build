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
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional

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
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPersistenceProvider

    @Test
    fun `getProjectSummary - not existing`() {
        every { projectRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectSummary(-1) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `getProjectSummary - everything OK`() {
        val project = dummyProject()
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(project)
        assertThat(persistence.getProjectSummary(PROJECT_ID)).isEqualTo(
            ProjectSummary(
                id = PROJECT_ID,
                acronym = project.acronym,
                status = project.currentStatus.status
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
}
