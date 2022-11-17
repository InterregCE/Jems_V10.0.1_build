package io.cloudflight.jems.server.project.repository.customCostOptions

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslId
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

internal class ProjectUnitCostPersistenceProviderTest : UnitTest() {

    companion object {

        private fun unitCost(id: Long, projectId: Long) = ProgrammeUnitCostEntity(
            id = id,
            projectId = projectId,
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "HUF",
            translatedValues = mutableSetOf(
                ProgrammeUnitCostTranslEntity(
                    ProgrammeUnitCostTranslId(id, SystemLanguage.EN),
                    name = "name EN",
                    description = "desc EN",
                    type = "type EN",
                    justification = "justification EN",
                )
            ),
            categories = mutableSetOf(
                ProgrammeUnitCostBudgetCategoryEntity(100L, id, BudgetCategory.StaffCosts),
                ProgrammeUnitCostBudgetCategoryEntity(101L, id, BudgetCategory.TravelAndAccommodationCosts),
            ),
        )

        private fun unitCostRow(id: Long, projectId: Long, category: BudgetCategory) = ProgrammeUnitCostRowImpl(
            id = id,
            projectId = projectId,
            oneCostCategory = false,
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "HUF",
            language = SystemLanguage.EN,
            name = "name EN",
            description = "desc EN",
            type = "type EN",
            justification = "justification EN",
            category = category,
        )

        private fun unitCostModel(id: Long, projectId: Long) = ProgrammeUnitCost(
            id = id,
            projectId = projectId,
            name = setOf(InputTranslation(SystemLanguage.EN, "name EN")),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type EN")),
            justification = setOf(InputTranslation(SystemLanguage.EN, "justification EN")),
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "HUF",
            isOneCostCategory = false,
            categories = mutableSetOf(BudgetCategory.StaffCosts, BudgetCategory.TravelAndAccommodationCosts),
        )
    }

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @MockK
    lateinit var repository: ProgrammeUnitCostRepository

    private lateinit var persistence: ProjectUnitCostPersistenceProvider

    @BeforeEach
    fun setup() {
        clearMocks(repository)
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectUnitCostPersistenceProvider(projectVersionUtils, repository)
    }

    @Test
    fun getAvailableUnitCostsForProjectId() {
        val projectId = 45L
        every { repository.findAllForProjectIdOrCall(projectId) } returns listOf(unitCost(225L, projectId))
        assertThat(persistence.getAvailableUnitCostsForProjectId(projectId, null))
            .containsExactly(unitCostModel(225L, projectId))
    }

    @Test
    fun `getAvailableUnitCostsForProjectId - historical`() {
        val projectId = 48L
        val timestamp = Timestamp.valueOf(LocalDateTime.now())

        every { projectVersionRepo.findTimestampByVersion(projectId, "4.8") } returns timestamp
        every { repository.findAllForProjectIdOrCallAsOfTimestamp(projectId, timestamp) } returns listOf(
            unitCostRow(228L, projectId, BudgetCategory.StaffCosts),
            unitCostRow(228L, projectId, BudgetCategory.TravelAndAccommodationCosts),
        )
        assertThat(persistence.getAvailableUnitCostsForProjectId(projectId, "4.8"))
            .containsExactly(unitCostModel(228L, projectId))
    }

    @Test
    fun getProjectUnitCost() {
        val projectId = 53L
        every { repository.findByIdAndProjectId(4L, projectId) } returns unitCost(4L, projectId)
        assertThat(persistence.getProjectUnitCost(projectId, 4L))
            .isEqualTo(unitCostModel(4L, projectId))
    }

    @Test
    fun `getProjectUnitCost - historical`() {
        val projectId = 55L
        val timestamp = Timestamp.valueOf(LocalDateTime.now())

        every { projectVersionRepo.findTimestampByVersion(projectId, "5.5") } returns timestamp
        every { repository.findByIdAndProjectIdAsOfTimestamp(7L, projectId, timestamp) } returns listOf(
            unitCostRow(7L, projectId, BudgetCategory.StaffCosts),
            unitCostRow(7L, projectId, BudgetCategory.TravelAndAccommodationCosts),
        )
        assertThat(persistence.getProjectUnitCost(projectId, 7L, "5.5"))
            .isEqualTo(unitCostModel(7L, projectId))
    }

    @ParameterizedTest(name = "existProjectUnitCost {0}")
    @ValueSource(booleans = [true, false])
    fun existProjectUnitCost(trueOrFalse: Boolean) {
        val projectId = 59L
        every { repository.existsByIdAndProjectId(10L, projectId = projectId) } returns trueOrFalse
        assertThat(persistence.existProjectUnitCost(projectId, unitCostId = 10L)).isEqualTo(trueOrFalse)
    }

    @Test
    fun getProjectUnitCostList() {
        val projectId = 62L
        every { repository.findAllByProjectId(projectId) } returns mutableListOf(unitCost(325L, projectId))
        assertThat(persistence.getProjectUnitCostList(projectId, null))
            .containsExactly(unitCostModel(325L, projectId))
    }

    @Test
    fun `getProjectUnitCostList - historical`() {
        val projectId = 65L
        val timestamp = Timestamp.valueOf(LocalDateTime.now())

        every { projectVersionRepo.findTimestampByVersion(projectId, "6.5") } returns timestamp
        every { repository.findAllByProjectIdAsOfTimestamp(projectId, timestamp) } returns listOf(
            unitCostRow(328L, projectId, BudgetCategory.StaffCosts),
            unitCostRow(328L, projectId, BudgetCategory.TravelAndAccommodationCosts),
        )
        assertThat(persistence.getProjectUnitCostList(projectId, "6.5"))
            .containsExactly(unitCostModel(328L, projectId))
    }

    @Test
    fun getCount() {
        val projectId = 64L
        every { repository.countAllByProjectId(projectId) } returns 22L
        assertThat(persistence.getCount(projectId)).isEqualTo(22L)
    }

    @Test
    fun deleteProjectUnitCost() {
        val projectId = 67L
        every { repository.deleteByIdAndProjectId(90L, projectId = projectId) } answers { }
        persistence.deleteProjectUnitCost(projectId, unitCostId = 90L)
        verify(exactly = 1) { repository.deleteByIdAndProjectId(90L, projectId = projectId) }
    }

}
