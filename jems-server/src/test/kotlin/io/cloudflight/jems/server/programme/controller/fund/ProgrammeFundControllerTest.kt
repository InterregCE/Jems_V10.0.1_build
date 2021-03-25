package io.cloudflight.jems.server.programme.controller.fund

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.get_fund.GetFundInteractor
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.fund.update_funds.UpdateFundsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeFundControllerTest : UnitTest() {

    companion object {

        private const val ID = 1L
        private val fund = ProgrammeFund(
            id = ID,
            selected = true,
            type = ProgrammeFundType.OTHER,
            abbreviation = setOf(InputTranslation(CS, "CS abbr")),
            description = setOf(
                InputTranslation(SK, "SK desc"),
                InputTranslation(CS, "CS desc")
            )

        )
        private val fundDto = ProgrammeFundDTO(
            id = ID,
            selected = true,
            type = ProgrammeFundTypeDTO.OTHER,
            abbreviation = setOf(
                InputTranslation(language = CS, translation = "CS abbr"),
            ),
            description = setOf(
                InputTranslation(language = SK, translation = "SK desc"),
                InputTranslation(language = CS, translation = "CS desc"),
            ),
        )

    }

    @MockK
    lateinit var getFund: GetFundInteractor

    @MockK
    lateinit var updateFunds: UpdateFundsInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeFundController

    @Test
    fun getProgrammeFundList() {
        every { getFund.getFunds() } returns listOf(fund)
        assertThat(controller.getProgrammeFundList()).containsExactly(fund.toDto())
    }

    @Test
    fun `should update Programme Funds`() {
        val fundsSlot = slot<List<ProgrammeFund>>()
        every { updateFunds.update(capture(fundsSlot)) } returnsArgument 0

        assertThat(controller.updateProgrammeFundList(setOf(fundDto.copy(id = null))))
            .containsExactly(fundDto.copy(id = 0))

        assertThat(fundsSlot.captured).containsExactly(
            ProgrammeFund(
                id = 0,
                selected = true,
                type = ProgrammeFundType.OTHER,
                abbreviation = setOf(InputTranslation(CS, "CS abbr")),
                description = setOf(InputTranslation(CS, "CS desc"), InputTranslation(SK, "SK desc"))
            )
        )
    }

}
