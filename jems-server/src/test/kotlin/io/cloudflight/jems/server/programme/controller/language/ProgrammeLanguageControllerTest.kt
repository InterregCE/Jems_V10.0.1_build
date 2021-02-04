package io.cloudflight.jems.server.programme.controller.language

import io.cloudflight.jems.api.programme.dto.language.AvailableProgrammeLanguagesDTO
import io.cloudflight.jems.api.programme.dto.language.ProgrammeLanguageDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.DE
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.language.get_languages.GetLanguagesInteractor
import io.cloudflight.jems.server.programme.service.language.model.AvailableProgrammeLanguages
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.cloudflight.jems.server.programme.service.language.update_languages.UpdateLanguagesInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeLanguageControllerTest : UnitTest() {

    companion object {
        private val language = ProgrammeLanguage(
            code = EN,
            ui = false,
            fallback = true,
            input = true
        )
        private val languageDto = ProgrammeLanguageDTO(
            code = EN,
            ui = false,
            fallback = true,
            input = true
        )
    }

    @MockK
    lateinit var getLanguages: GetLanguagesInteractor

    @MockK
    lateinit var updateLanguages: UpdateLanguagesInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeLanguageController

    @Test
    fun get() {
        every { getLanguages.getAllLanguages() } returns listOf(language)
        assertThat(controller.get()).containsExactly(languageDto)
    }

    @Test
    fun getAvailableProgrammeLanguages() {
        every { getLanguages.getAvailableLanguages() } returns AvailableProgrammeLanguages(
            inputLanguages = setOf(EN, SK, DE),
            systemLanguages = setOf(EN),
            fallbackLanguage = CS
        )
        assertThat(controller.getAvailableProgrammeLanguages()).isEqualTo(AvailableProgrammeLanguagesDTO(
            inputLanguages = setOf(EN.name, SK.name, DE.name),
            systemLanguages = setOf(EN.name),
            fallbackLanguage = CS.name,
        ))
    }

    @Test
    fun update() {
        val slotLanguages = slot<List<ProgrammeLanguage>>()
        every { updateLanguages.updateLanguages(capture(slotLanguages)) } returnsArgument 0
        assertThat(controller.update(listOf(languageDto))).containsExactly(languageDto)
        assertThat(slotLanguages.captured).containsExactly(language)
    }

}
