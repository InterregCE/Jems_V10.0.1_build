package io.cloudflight.jems.server.project.service.unitcost.get_project_unit_costs

import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetProjectUnitCostsTest {

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @InjectMockKs
    lateinit var getProjectUnitCost: GetProjectUnitCosts

    @Test
    fun getUnitCosts() {
        every { projectBudgetPersistence.getProjectUnitCosts(1L) } returns emptyList()
        assertThat(getProjectUnitCost.getProjectUnitCost(1L)).containsExactly()
    }
}