package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectUnitCostList

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetProjectUnitCostListTest : UnitTest() {

    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectUnitCostList

    @BeforeEach
    fun resetMocks() {
        clearMocks(projectUnitCostPersistence)
    }

    @Test
    fun getUnitCostList() {
        val unitCost = mockk<ProgrammeUnitCost>()
        every { projectUnitCostPersistence.getProjectUnitCostList(945L) } returns listOf(unitCost)
        assertThat(interactor.getUnitCostList(945L)).containsExactly(unitCost)
    }

    @Test
    fun getUnitCost() {
        val unitCost = mockk<ProgrammeUnitCost>()
        every { projectUnitCostPersistence.getProjectUnitCost(456L, unitCostId = 22L) } returns unitCost
        assertThat(interactor.getUnitCost(456L, unitCostId = 22L)).isEqualTo(unitCost)
    }

}
