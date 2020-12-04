package io.cloudflight.jems.server.programme.service.costoption.delete_unit_cost

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteUnitCostInteractorTest {

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    private lateinit var deleteUnitCostInteractor: DeleteUnitCostInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        deleteUnitCostInteractor = DeleteUnitCost(persistence)
    }

    @Test
    fun `delete unit cost - OK`() {
        every { persistence.deleteUnitCost(1L) } answers {}
        deleteUnitCostInteractor.deleteUnitCost(1L)
        verify { persistence.deleteUnitCost(1L) }
    }

    @Test
    fun `delete unit cost - not existing`() {
        every { persistence.deleteUnitCost(-1L) } throws ResourceNotFoundException("programmeUnitCost")
        assertThrows<ResourceNotFoundException> { deleteUnitCostInteractor.deleteUnitCost(-1L) }
    }

}
