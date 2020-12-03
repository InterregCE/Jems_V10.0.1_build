package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.server.programme.service.costoption.create_lump_sum.CreateLumpSumInteractor
import io.cloudflight.jems.server.programme.service.costoption.delete_lump_sum.DeleteLumpSumInteractor
import io.cloudflight.jems.server.programme.service.costoption.get_lump_sum.GetLumpSumInteractor
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.update_lump_sum.UpdateLumpSumInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class ProgrammeCostOptionControllerTest {

    companion object {

        private val testLumpSum = ProgrammeLumpSum(
            id = 1,
            name = "LS1",
            description = "test lump sum 1",
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(StaffCosts, OfficeAndAdministrationCosts),
        )

        private val expectedLumpSum = ProgrammeLumpSumDTO(
            id = 1,
            name = "LS1",
            description = "test lump sum 1",
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(StaffCosts, OfficeAndAdministrationCosts),
        )

    }

    @MockK
    lateinit var getLumpSum: GetLumpSumInteractor

    @MockK
    lateinit var createLumpSum: CreateLumpSumInteractor

    @MockK
    lateinit var updateLumpSum: UpdateLumpSumInteractor

    @MockK
    lateinit var deleteLumpSum: DeleteLumpSumInteractor

    private lateinit var controller: ProgrammeCostOptionController

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        controller = ProgrammeCostOptionController(
            getLumpSum,
            createLumpSum,
            updateLumpSum,
            deleteLumpSum,
        )
    }

    @Test
    fun getLumpSums() {
        every { getLumpSum.getLumpSums(any()) } returns PageImpl(listOf(testLumpSum))
        val lumpSum = controller.getProgrammeLumpSums(Pageable.unpaged())
        assertThat(lumpSum.content).containsExactly(expectedLumpSum)
    }

    @Test
    fun createProgrammeLumpSum() {
        val slotLumpSum = slot<ProgrammeLumpSum>()
        every { createLumpSum.createLumpSum(capture(slotLumpSum)) } returnsArgument 0

        assertThat(controller.createProgrammeLumpSum(expectedLumpSum)).isEqualTo(expectedLumpSum)
        assertThat(slotLumpSum.captured).isEqualTo(testLumpSum)
    }

    @Test
    fun updateProgrammeLumpSum() {
        val slotLumpSum = slot<ProgrammeLumpSum>()
        every { updateLumpSum.updateLumpSum(capture(slotLumpSum)) } returnsArgument 0

        assertThat(controller.updateProgrammeLumpSum(expectedLumpSum)).isEqualTo(expectedLumpSum)
        assertThat(slotLumpSum.captured).isEqualTo(testLumpSum)
    }

    @Test
    fun deleteProgrammeLumpSum() {
        every { deleteLumpSum.deleteLumpSum(5L) } answers {}
        controller.deleteProgrammeLumpSum(5L)
        verify { deleteLumpSum.deleteLumpSum(5L) }
    }

}
