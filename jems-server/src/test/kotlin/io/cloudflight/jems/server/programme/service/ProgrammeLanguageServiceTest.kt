package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.SystemLanguage
import io.cloudflight.jems.api.programme.dto.InputProgrammeLanguage
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLanguage
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.programme.entity.ProgrammeLanguage
import io.cloudflight.jems.server.programme.repository.ProgrammeLanguageRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * tests ProgrammeLanguageService methods including ProgrammeLanguageMapper.
 */
internal class ProgrammeLanguageServiceTest {

    private val existingProgrammeLanguage = ProgrammeLanguage(SystemLanguage.EN, true, true, true)

    @MockK
    lateinit var programmeLanguageRepository: ProgrammeLanguageRepository

    @RelaxedMockK
    lateinit var auditService: AuditService
    lateinit var programmeLanguageService: ProgrammeLanguageServiceImpl

    @MockK
    lateinit var mockedList: List<InputProgrammeLanguage>

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeLanguageService = ProgrammeLanguageServiceImpl(
            programmeLanguageRepository,
            auditService
        )
    }

    @Test
    fun get() {
        every { programmeLanguageRepository.findAll() } returns listOf(existingProgrammeLanguage)

        val programmeData = programmeLanguageService.get()

        assertThat(programmeData).containsExactly(existingProgrammeLanguage.toOutputProgrammeLanguage())
    }

    @Test
    fun `update existing programme language`() {
        val languageInputEN = InputProgrammeLanguage(SystemLanguage.EN, true, false, true)
        val languageInputDE = InputProgrammeLanguage(SystemLanguage.DE, true, false, false)
        val languageUpdatedEN = OutputProgrammeLanguage(SystemLanguage.EN, true, false, true).toEntity()
        val languageUpdatedDE = OutputProgrammeLanguage(SystemLanguage.DE, true, false, false).toEntity()

        every { programmeLanguageRepository.saveAll(any<List<ProgrammeLanguage>>()) } returns listOf(languageUpdatedEN, languageUpdatedDE)

        val result = programmeLanguageService.update(listOf(languageInputEN, languageInputDE))

        assertThat(result).containsExactly(languageUpdatedEN.toOutputProgrammeLanguage(), languageUpdatedDE.toOutputProgrammeLanguage())

        val eventInput = slot<AuditCandidate>()
        val eventUI = slot<AuditCandidate>()
        verifyOrder {
            auditService.logEvent(capture(eventUI))
            auditService.logEvent(capture(eventInput))
        }
        with(eventUI) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_UI_LANGUAGES_CHANGED)
            assertThat(captured.description).isEqualTo("Programme UI languages available set to:\n" +
                "EN, DE")
        }
        with(eventInput) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_INPUT_LANGUAGES_CHANGED)
            assertThat(captured.description).isEqualTo("Programme INPUT languages set to:\n" +
                "EN")
        }
    }

    @Test
    fun `update fails on too many programme languages`() {
        every { mockedList.size } returns 51

        assertThat(
            assertThrows<I18nValidationException> { programmeLanguageService.update(mockedList) }.i18nKey
        ).isEqualTo("programme.language.max.allowed.reached")
    }
}
