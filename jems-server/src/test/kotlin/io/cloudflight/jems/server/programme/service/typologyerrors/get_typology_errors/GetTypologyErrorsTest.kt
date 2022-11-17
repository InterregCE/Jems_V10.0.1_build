package io.cloudflight.jems.server.programme.service.typologyerrors.get_typology_errors

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.GetTypologyErrors
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GetTypologyErrorsTest : UnitTest() {

    companion object {
        private val typologyErrors = TypologyErrors(
            id = 1,
            description = "Sample description"
        )
    }

    @MockK
    lateinit var persistence: ProgrammeTypologyErrorsPersistence

    @InjectMockKs
    lateinit var getTypologyErrors: GetTypologyErrors

    @Test
    fun `should get programme typology errors`() {
        every { persistence.getAllTypologyErrors() } returns listOf(typologyErrors)
        Assertions.assertThat(getTypologyErrors.getTypologyErrors()).containsExactly(typologyErrors)
    }
}
