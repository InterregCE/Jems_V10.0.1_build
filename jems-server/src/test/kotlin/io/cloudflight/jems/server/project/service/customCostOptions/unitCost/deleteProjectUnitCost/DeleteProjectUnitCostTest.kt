package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.deleteProjectUnitCost

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteProjectUnitCostTest : UnitTest() {

    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence
    @MockK
    lateinit var projectPartnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @InjectMockKs
    lateinit var interactor: DeleteProjectUnitCost

    @BeforeEach
    fun resetMocks() {
        clearMocks(projectUnitCostPersistence, projectPartnerBudgetCostsPersistence)
    }

    @Test
    fun deleteProjectUnitCost() {
        val projectId = 75L
        every { projectUnitCostPersistence.existProjectUnitCost(projectId, 20L) } returns true
        every { projectPartnerBudgetCostsPersistence.isUnitCostUsed(20L) } returns false
        every { projectUnitCostPersistence.deleteProjectUnitCost(projectId, 20L) } answers { }

        interactor.deleteProjectUnitCost(projectId, unitCostId = 20L)
        verify(exactly = 1) { projectUnitCostPersistence.deleteProjectUnitCost(projectId, 20L) }
    }

    @Test
    fun `deleteProjectUnitCost - not found`() {
        val projectId = 77L
        every { projectUnitCostPersistence.existProjectUnitCost(projectId, -1L) } returns false

        assertThrows<ProjectUnitCostNotFound> { interactor.deleteProjectUnitCost(projectId, unitCostId = -1L) }
        verify(exactly = 0) { projectUnitCostPersistence.deleteProjectUnitCost(any(), any()) }
    }


    @Test
    fun `deleteProjectUnitCost - unit cost is in use`() {
        val projectId = 78L
        every { projectUnitCostPersistence.existProjectUnitCost(projectId, 25L) } returns true
        every { projectPartnerBudgetCostsPersistence.isUnitCostUsed(25L) } returns true

        assertThrows<ProjectUnitCostIsInUse> { interactor.deleteProjectUnitCost(projectId, unitCostId = 25L) }
        verify(exactly = 0) { projectUnitCostPersistence.deleteProjectUnitCost(any(), any()) }
    }

}
