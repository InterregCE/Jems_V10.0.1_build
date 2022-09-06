package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectAvailableUnitCost

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

internal class GetProjectAvailableUnitCostTest : UnitTest() {

    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectAvailableUnitCost

    @BeforeEach
    fun resetMocks() {
        clearMocks(projectUnitCostPersistence)
    }

    @Test
    fun getAvailableUnitCost() {
        val result = mockk<ProgrammeUnitCost>()
        every { projectUnitCostPersistence.getAvailableUnitCostsForProjectId(945L) } returns listOf(result)
        assertThat(interactor.getAvailableUnitCost(945L)).containsExactly(result)
    }

}
