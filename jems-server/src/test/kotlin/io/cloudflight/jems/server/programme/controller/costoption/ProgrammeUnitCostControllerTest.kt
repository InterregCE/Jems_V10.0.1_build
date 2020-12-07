package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.EquipmentCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.TravelAndAccommodationCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.server.programme.service.costoption.create_unit_cost.CreateUnitCostInteractor
import io.cloudflight.jems.server.programme.service.costoption.delete_unit_cost.DeleteUnitCostInteractor
import io.cloudflight.jems.server.programme.service.costoption.get_unit_cost.GetUnitCostInteractor
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.update_unit_cost.UpdateUnitCostInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class ProgrammeUnitCostControllerTest {

    companion object {

        private val testUnitCost = ProgrammeUnitCost(
            id = 1,
            name = "UC1",
            description = "test unit cost 1",
            type = "type 1",
            costPerUnit = BigDecimal.ONE,
            categories = setOf(EquipmentCosts, TravelAndAccommodationCosts),
        )

        private val expectedUnitCost = ProgrammeUnitCostDTO(
            id = 1,
            name = "UC1",
            description = "test unit cost 1",
            type = "type 1",
            costPerUnit = BigDecimal.ONE,
            categories = setOf(EquipmentCosts, TravelAndAccommodationCosts),
        )

    }

    @MockK
    lateinit var getUnitCost: GetUnitCostInteractor

    @MockK
    lateinit var createUnitCost: CreateUnitCostInteractor

    @MockK
    lateinit var updateUnitCost: UpdateUnitCostInteractor

    @MockK
    lateinit var deleteUnitCost: DeleteUnitCostInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeUnitCostController

    @Test
    fun `should get ProgrammeUnitCosts`() {
        every { getUnitCost.getUnitCosts(any()) } returns PageImpl(listOf(testUnitCost))
        val unitCost = controller.getProgrammeUnitCosts(Pageable.unpaged())
        assertThat(unitCost.content).containsExactly(expectedUnitCost)
    }

    @Test
    fun `should create a new ProgrammeUnitCost and returns it when input is valid`() {
        val slotUnitCost = slot<ProgrammeUnitCost>()
        every { createUnitCost.createUnitCost(capture(slotUnitCost)) } returnsArgument 0

        assertThat(controller.createProgrammeUnitCost(expectedUnitCost)).isEqualTo(expectedUnitCost)
        assertThat(slotUnitCost.captured).isEqualTo(testUnitCost)
    }

    @Test
    fun `should update existing ProgrammeUnitCost and returns it when input is valid`() {
        val slotUnitCost = slot<ProgrammeUnitCost>()
        every { updateUnitCost.updateUnitCost(capture(slotUnitCost)) } returnsArgument 0

        assertThat(controller.updateProgrammeUnitCost(expectedUnitCost)).isEqualTo(expectedUnitCost)
        assertThat(slotUnitCost.captured).isEqualTo(testUnitCost)
    }

    @Test
    fun `should delete ProgrammeUnitCost`() {
        every { deleteUnitCost.deleteUnitCost(5L) } answers {}
        controller.deleteProgrammeUnitCost(5L)
        verify { deleteUnitCost.deleteUnitCost(5L) }
    }

}
