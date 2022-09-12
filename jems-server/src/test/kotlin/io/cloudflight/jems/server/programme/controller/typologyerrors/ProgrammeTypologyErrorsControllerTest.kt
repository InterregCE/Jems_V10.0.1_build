package io.cloudflight.jems.server.programme.controller.typologyerrors

import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsDTO
import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.GetTypologyErrorsInteractor
import io.cloudflight.jems.server.programme.service.typologyerrors.UpdateTypologyErrorsInteractor
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ProgrammeTypologyErrorsControllerTest: UnitTest() {

    companion object {
        private const val ID = 1L
        private val typologyErrors = TypologyErrors(
            id = ID,
            description = "Sample description"
        )
        private val typologyErrorsDTO = TypologyErrorsDTO(
            id = ID,
            description = "Sample description",
        )
    }

    @MockK
    lateinit var getTypologyErrors: GetTypologyErrorsInteractor

    @MockK
    lateinit var updateTypologyErrors: UpdateTypologyErrorsInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeTypologyErrorsController

    @Test
    fun `should get programme typology errors`() {
        every { getTypologyErrors.getTypologyErrors() } returns listOf(typologyErrors)
        Assertions.assertThat(controller.getTypologyErrors())
            .containsExactly(typologyErrorsDTO)
    }

    @Test
    fun `should update programme typology errors`() {
        val deleteIdsSlot = slot<List<Long>>()
        val toPersistSlot = slot<List<TypologyErrors>>()
        every { updateTypologyErrors.updateTypologyErrors(capture(deleteIdsSlot), capture(toPersistSlot)) } returnsArgument 1

        val toUpdate = TypologyErrorsUpdateDTO(
            toDeleteIds = listOf(20L),
            toPersist = listOf(typologyErrorsDTO.copy(id = null)),
        )
        Assertions.assertThat(controller.updateTypologyErrors(toUpdate))
            .containsExactly(typologyErrorsDTO.copy(id = 0))
        Assertions.assertThat(deleteIdsSlot.captured).containsExactly(20L)
        Assertions.assertThat(toPersistSlot.captured).containsExactly(
            TypologyErrors(
                id = 0,
                description = "Sample description",
            )
        )
    }
}
