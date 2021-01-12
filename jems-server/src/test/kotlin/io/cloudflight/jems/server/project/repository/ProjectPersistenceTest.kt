package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
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

        private val dummyCall = callWithId(10).copy(
            id = CALL_ID,
            name = "call name",
            startDate = startDate,
            endDate = endDate,
            lengthOfPeriod = 9,
            flatRates = mutableSetOf(
                ProjectCallFlatRateEntity(setupId = FlatRateSetupId(CALL_ID, FlatRateType.STAFF_COSTS), rate = 15, isAdjustable = true),
            ),
            lumpSums = setOf(
                ProgrammeLumpSumEntity(
                    id = 32,
                    name = "LumpSum",
                    description = "pls 32",
                    cost = BigDecimal.TEN,
                    splittingAllowed = false,
                    phase = ProgrammeLumpSumPhase.Preparation,
                    categories = mutableSetOf(
                        ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = 12, category = BudgetCategory.EquipmentCosts),
                        ProgrammeLumpSumBudgetCategoryEntity(programmeLumpSumId = 13, category = BudgetCategory.TravelAndAccommodationCosts),
                    ),
                ),
            ),
            unitCosts = setOf(
                ProgrammeUnitCostEntity(
                    id = 4,
                    name = "UnitCost",
                    description = "pus 4",
                    type = "type of unit cost",
                    costPerUnit = BigDecimal.ONE,
                    categories = mutableSetOf(
                        ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = 14, category = BudgetCategory.ExternalCosts),
                        ProgrammeUnitCostBudgetCategoryEntity(programmeUnitCostId = 15, category = BudgetCategory.OfficeAndAdministrationCosts),
                    ),
                ),
            ),
        )

        private val dummyProject = ProjectEntity(
            id = PROJECT_ID,
            call = dummyCall,
            acronym = "Test Project",
            applicant = dummyCall.creator,
            projectStatus = ProjectStatus(id = 1, status = ProjectApplicationStatus.DRAFT, user = dummyCall.creator),
            periods = listOf(
                ProjectPeriodEntity(
                    id = ProjectPeriodId(projectId = PROJECT_ID, number = 1),
                    start = 1,
                    end = 2,
                )
            ),
        )
    }

    @MockK
    lateinit var projectRepository: ProjectRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPersistenceProvider

    @Test
    fun `getProject - not existing`() {
        every { projectRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProject(-1) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `getProject - everything OK`() {
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(dummyProject)
        assertThat(persistence.getProject(PROJECT_ID)).isEqualTo(
            Project(
                id = PROJECT_ID,
                periods = listOf(
                    ProjectPeriod(number = 1, start = 1, end = 2),
                ),
            )
        )
    }

    @Test
    fun `getProjectCallSettingsForProject - not existing`() {
        every { projectRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectCallSettingsForProject(-1) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `getProjectCallSettingsForProject - everything OK`() {
        every { projectRepository.findById(PROJECT_ID) } returns Optional.of(dummyProject)
        assertThat(persistence.getProjectCallSettingsForProject(PROJECT_ID)).isEqualTo(
            ProjectCallSettings(
                callId = CALL_ID,
                callName = "call name",
                startDate = startDate,
                endDate = endDate,
                lengthOfPeriod = 9,
                flatRates = setOf(
                    ProjectCallFlatRate(type = FlatRateType.STAFF_COSTS, rate = 15, isAdjustable = true),
                ),
                lumpSums = listOf(
                    ProgrammeLumpSum(
                        id = 32,
                        name = "LumpSum",
                        description = "pls 32",
                        cost = BigDecimal.TEN,
                        splittingAllowed = false,
                        phase = ProgrammeLumpSumPhase.Preparation,
                        categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                    ),
                ),
                unitCosts = listOf(
                    ProgrammeUnitCost(
                        id = 4,
                        name = "UnitCost",
                        description = "pus 4",
                        type = "type of unit cost",
                        costPerUnit = BigDecimal.ONE,
                        categories = setOf(BudgetCategory.ExternalCosts, BudgetCategory.OfficeAndAdministrationCosts),
                    ),
                ),
            ),
        )
    }

}
