package io.cloudflight.jems.server.programme.service.indicator.create_result_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test


internal class CreateResultIndicatorTest : IndicatorsBaseTest() {

    @MockK
    lateinit var persistence: ResultIndicatorPersistence

    @InjectMockKs
    private lateinit var createResultIndicator: CreateResultIndicator

    @Test
    fun `should create and return result indicator when there is no problem`() {
        val resultIndicator = buildResultIndicatorInstance(id = 0L)
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { persistence.saveResultIndicator(resultIndicator) } returns resultIndicatorDetail
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                resultIndicator.id,
                resultIndicator.identifier
            )
        } returns false
        every { persistence.getCountOfResultIndicators() } returns 0

        assertThat(createResultIndicator.createResultIndicator(resultIndicator))
            .isEqualTo(resultIndicatorDetail)

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_ADDED)
            assertThat(description).isEqualTo("Programme indicator ID01 has been added")
        }
    }

    @Test
    fun `should throw IdentifierIsUsedException when result indicator identifier is already used`() {
        val resultIndicator = buildResultIndicatorInstance(id = 0L)
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                resultIndicator.id,
                resultIndicator.identifier
            )
        } returns true

        val exception = catchThrowableOfType(
            { createResultIndicator.createResultIndicator(resultIndicator) },
            IdentifierIsUsedException::class.java
        )
        assertThat(exception.formErrors["identifier"]).isEqualTo(I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"))
    }

    @Test
    fun `should throw ResultIndicatorsCountExceedException when count of result indicators exceed the limit`() {
        val resultIndicator = buildResultIndicatorInstance(id = 0L)
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(resultIndicator.id, resultIndicator.identifier)
        } returns false
        every { persistence.getCountOfResultIndicators() } returns 60

        assertThatExceptionOfType(ResultIndicatorsCountExceedException::class.java).isThrownBy {
            createResultIndicator.createResultIndicator(resultIndicator)
        }

    }

}
