package io.cloudflight.jems.server.call.service.costoption.update_call_cost_options

import io.cloudflight.jems.server.call.service.costoption.CallCostOptionsPersistence
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UpdateCallCostOptionsInteractorTest {

    @MockK
    lateinit var persistence: CallCostOptionsPersistence

    @InjectMockKs
    private lateinit var updateCallCostOptions: UpdateCallCostOptions

    @Test
    fun updateLumpSums() {
        every { persistence.updateProjectCallLumpSum(1, setOf(2, 3)) } answers {}
        updateCallCostOptions.updateLumpSums(1, setOf(2, 3))

        verify { persistence.updateProjectCallLumpSum(1, setOf(2, 3)) }
        confirmVerified(persistence)
    }

    @Test
    fun updateUnitCosts() {
        every { persistence.updateProjectCallUnitCost(1, setOf(2, 3)) } answers {}
        updateCallCostOptions.updateUnitCosts(1, setOf(2, 3))

        verify { persistence.updateProjectCallUnitCost(1, setOf(2, 3)) }
        confirmVerified(persistence)
    }

}
