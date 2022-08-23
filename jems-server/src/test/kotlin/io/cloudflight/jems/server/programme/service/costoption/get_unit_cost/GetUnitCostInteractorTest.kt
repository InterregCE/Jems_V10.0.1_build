package io.cloudflight.jems.server.programme.service.costoption.get_unit_cost

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class GetUnitCostInteractorTest {

    companion object {

        private val testUnitCost = ProgrammeUnitCost(
            id = 1,
            projectId = null,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(StaffCosts, OfficeAndAdministrationCosts),
        )

    }

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    private lateinit var getUnitCostInteractor: GetUnitCostInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        getUnitCostInteractor = GetUnitCost(persistence)
    }

    @Test
    fun `get unit costs`() {
        every { persistence.getUnitCosts() } returns listOf(testUnitCost)
        assertThat(getUnitCostInteractor.getUnitCosts()).containsExactly(testUnitCost.copy())
    }

    @Test
    fun `get unit cost`() {
        every { persistence.getUnitCost(1L) } returns testUnitCost
        assertThat(getUnitCostInteractor.getUnitCost(1L)).isEqualTo(testUnitCost.copy())
    }

    @Test
    fun `get unit cost - not existing`() {
        every { persistence.getUnitCost(-1L) } throws ResourceNotFoundException("programmeUnitCost")
        assertThrows<ResourceNotFoundException> { getUnitCostInteractor.getUnitCost(-1L) }
    }

}
