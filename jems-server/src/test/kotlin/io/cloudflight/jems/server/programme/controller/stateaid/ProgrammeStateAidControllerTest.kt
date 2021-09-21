package io.cloudflight.jems.server.programme.controller.stateaid

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidDTO
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidUpdateDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.stateaid.get_stateaid.GetStateAidInteractor
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.programme.service.stateaid.update_stateaid.UpdateStateAidInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProgrammeStateAidControllerTest : UnitTest() {

    companion object {
        private val stateAid = ProgrammeStateAid(
            id = 2L,
            measure = ProgrammeStateAidMeasure.OTHER_1,
            threshold = BigDecimal.TEN,
            maxIntensity = BigDecimal.TEN,
            name = setOf(InputTranslation(language = SystemLanguage.EN, translation = "name")),
            abbreviatedName = setOf(InputTranslation(language = SystemLanguage.EN, translation = "abbreviatedName")),
            schemeNumber = "schemeNumber"
        )
        private val stateAidDto = ProgrammeStateAidDTO(
            id = stateAid.id,
            measure = ProgrammeStateAidMeasure.OTHER_1,
            threshold = BigDecimal.TEN,
            maxIntensity = BigDecimal.TEN,
            name = setOf(InputTranslation(language = EN, translation = "name")),
            abbreviatedName = setOf(InputTranslation(language = EN, translation = "abbreviatedName")),
            schemeNumber = "schemeNumber"
        )
    }

    @MockK
    lateinit var getStateAid: GetStateAidInteractor
    @MockK
    lateinit var updateStateAid: UpdateStateAidInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeStateAidController

    @Test
    fun getProgrammeStateAidList() {
        every { getStateAid.getStateAidList() } returns listOf(stateAid)
        assertThat(controller.getProgrammeStateAidList()).containsExactly(stateAidDto)
    }

    @Test
    fun updateProgrammeStateAids() {
        val stateAidsSlot = slot<List<ProgrammeStateAid>>()
        every { updateStateAid.updateStateAids(setOf(3L), capture(stateAidsSlot)) } returns listOf(stateAid)

        assertThat(controller.updateProgrammeStateAids(
            ProgrammeStateAidUpdateDTO(
                toDeleteIds = setOf(3L),
                toPersist = listOf(stateAidDto)
            )))
            .containsExactly(stateAidDto)

        assertThat(stateAidsSlot.captured).containsExactly(stateAid)
    }

}
