package io.cloudflight.jems.server.programme.service.language.update_languages

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.DE
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EL
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateLanguagesInteractorTest : UnitTest() {

    companion object {
        private val languageInputEN = ProgrammeLanguage(
            code = EN, ui = true, fallback = false, input = true
        )
        private val languageInputDE = ProgrammeLanguage(
            code = DE, ui = true, fallback = false, input = false
        )
    }

    @MockK
    lateinit var persistence: ProgrammeLanguagePersistence

    @MockK
    lateinit var callRepository: CallRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    @InjectMockKs
    lateinit var updateLanguages: UpdateLanguages

    @MockK
    lateinit var mockedList: List<ProgrammeLanguage>

    @Test
    fun `update existing programme language`() {
        val slotLanguages = slot<List<ProgrammeLanguage>>()
        every { persistence.isProgrammeSetupRestricted() } returns false
        every { persistence.updateLanguages(capture(slotLanguages)) } returnsArgument 0

        assertThat(updateLanguages.updateLanguages(listOf(languageInputEN, languageInputDE))).containsExactly(
            languageInputEN, languageInputDE
        )
        assertThat(slotLanguages.captured).containsExactly(languageInputEN, languageInputDE)

        val eventInput = slot<AuditCandidate>()
        val eventUI = slot<AuditCandidate>()
        verifyOrder {
            auditService.logEvent(capture(eventUI))
            auditService.logEvent(capture(eventInput))
        }
        with(eventUI) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_UI_LANGUAGES_CHANGED)
            assertThat(captured.description).isEqualTo("Programme UI languages available set to:\nEN, DE")
        }
        with(eventInput) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_INPUT_LANGUAGES_CHANGED)
            assertThat(captured.description).isEqualTo("Programme INPUT languages set to:\nEN")
        }
    }

    @Test
    fun `update fails on too many programme languages`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
        every { mockedList.size } returns 41

        val ex = assertThrows<I18nValidationException> { updateLanguages.updateLanguages(mockedList) }
        assertThat(ex.i18nKey).isEqualTo("programme.language.max.allowed.reached")
    }

    @Test
    fun `update fails on empty input languages`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
        val ex = assertThrows<I18nValidationException> {
            updateLanguages.updateLanguages(
                listOf(
                    ProgrammeLanguage(
                        code = EL, ui = true, fallback = true, input = false
                    )
                )
            )
        }
        assertThat(ex.i18nKey).isEqualTo("programme.language.min.allowed.input.languages")
    }

    @Test
    fun `update fails on too many input languages`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
        val ex = assertThrows<I18nValidationException> {
            updateLanguages.updateLanguages(getInputLanguages(CS, DE, EL, EN, SK))
        }
        assertThat(ex.i18nKey).isEqualTo("programme.language.max.allowed.input.languages")
    }

    @Test
    fun `update fails on call already published`() {
        every { persistence.isProgrammeSetupRestricted() } returns true
        every { persistence.getLanguages() } returns listOf(languageInputEN, languageInputDE)
        assertThrows<UpdateLanguagesWhenProgrammeSetupRestricted> { updateLanguages.updateLanguages(listOf(languageInputEN)) }
    }

    private fun getInputLanguages(vararg language: SystemLanguage) = language.toList().map {
        ProgrammeLanguage(
            code = it, ui = false, fallback = false, input = true
        )
    }
}
