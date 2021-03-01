package io.cloudflight.jems.server.programme.repository.language

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.programme.entity.language.ProgrammeLanguageEntity
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeLanguagePersistenceTest : UnitTest() {

    companion object {
        private val language = ProgrammeLanguage(
            code = SystemLanguage.EN,
            ui = false,
            fallback = true,
            input = true
        )
        private val languageEntity = ProgrammeLanguageEntity(
            code = SystemLanguage.EN,
            ui = false,
            fallback = true,
            input = true
        )
    }

    @MockK
    lateinit var repository: ProgrammeLanguageRepository

    @MockK
    lateinit var callRepository: CallRepository

    @InjectMockKs
    lateinit var persistence: ProgrammeLanguagePersistenceProvider

    @Test
    fun getLanguages() {
        every { repository.findTop40ByOrderByCode() } returns listOf(languageEntity)
        assertThat(persistence.getLanguages()).containsExactly(language)
    }

    @Test
    fun updateLanguages() {
        val slot = slot<Iterable<ProgrammeLanguageEntity>>()
        every { repository.saveAll(capture(slot)) } returnsArgument 0
        assertThat(persistence.updateLanguages(listOf(language))).containsExactly(language)
        assertThat(slot.captured).containsExactly(languageEntity)
    }

}
