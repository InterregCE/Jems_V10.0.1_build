package io.cloudflight.jems.server.programme.service.stateaid.get_state_aids

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.get_stateaid.GetStateAid
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetStateAidTest : UnitTest() {


    private val stateAid = ProgrammeStateAid(
        id = 14,
        measure = ProgrammeStateAidMeasure.OTHER_1,
        name = setOf(InputTranslation(language = SystemLanguage.EN, translation = "EN name")),
        abbreviatedName = setOf(InputTranslation(language = SystemLanguage.EN, translation = "EN abbName")),
        schemeNumber = "Sch",
        maxIntensity = BigDecimal(50),
        threshold = BigDecimal(30),
        comments = setOf(InputTranslation(language = SystemLanguage.EN, translation = "EN comm"))
    )

    @MockK
    lateinit var persistence: ProgrammeStateAidPersistence

    @InjectMockKs
    lateinit var getStateAid: GetStateAid

    @Test
    fun getStateAids() {
        every { persistence.getStateAidList() } returns listOf(stateAid)
        assertThat(getStateAid.getStateAidList()).containsExactly(stateAid)
    }
}
