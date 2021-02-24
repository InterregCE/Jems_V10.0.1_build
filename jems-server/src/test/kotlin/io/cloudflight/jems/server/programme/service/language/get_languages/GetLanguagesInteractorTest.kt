package io.cloudflight.jems.server.programme.service.language.get_languages

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.AvailableProgrammeLanguages
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetLanguagesInteractorTest : UnitTest() {

    companion object {
        private val inputEN = ProgrammeLanguage(
            code = EN,
            ui = false,
            fallback = true,
            input = true
        )
        private val uiSK = ProgrammeLanguage(
            code = SK,
            ui = true,
            fallback = false,
            input = false
        )
    }

    @MockK
    lateinit var persistence: ProgrammeLanguagePersistence

    @InjectMockKs
    lateinit var getLanguages: GetLanguages

    @Test
    fun getAllLanguages() {
        every { persistence.getLanguages() } returns listOf(inputEN, uiSK)
        assertThat(getLanguages.getAllLanguages()).containsExactly(inputEN, uiSK)
    }

    @Test
    fun getAvailableLanguages() {
        every { persistence.getLanguages() } returns listOf(inputEN, uiSK)
        assertThat(getLanguages.getAvailableLanguages()).isEqualTo(AvailableProgrammeLanguages(
            inputLanguages = setOf(EN),
            systemLanguages = setOf(SK),
            fallbackLanguage = EN,
        ))
    }

}
