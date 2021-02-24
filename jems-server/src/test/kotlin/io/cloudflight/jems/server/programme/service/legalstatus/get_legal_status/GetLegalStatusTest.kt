package io.cloudflight.jems.server.programme.service.legalstatus.get_legal_status

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.get_legal_statuses.GetLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetLegalStatusTest : UnitTest() {

    companion object {
        private val legalStatus = ProgrammeLegalStatus(
            id = 14,
            translatedValues = setOf(ProgrammeLegalStatusTranslatedValue(language = EN, description = "EN desc"))
        )
    }

    @MockK
    lateinit var persistence: ProgrammeLegalStatusPersistence

    @InjectMockKs
    lateinit var getLegalStatus: GetLegalStatus

    @Test
    fun getLegalStatuses() {
        every { persistence.getMax20Statuses() } returns listOf(legalStatus)
        assertThat(getLegalStatus.getLegalStatuses()).containsExactly(legalStatus)
    }

}
