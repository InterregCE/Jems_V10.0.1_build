package io.cloudflight.jems.server.programme.service.indicator.create_output_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
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

internal class CreateOutputIndicatorTest : IndicatorsBaseTest() {

    @MockK
    lateinit var persistence: OutputIndicatorPersistence

    @MockK
    lateinit var resultIndicatorPersistence: ResultIndicatorPersistence

    @InjectMockKs
    private lateinit var createOutputIndicator: CreateOutputIndicator

    @Test
    fun `should create and return output indicator when there is no problem`() {
        val outputIndicator = buildOutputIndicatorInstance(id = 0L)
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every { persistence.saveOutputIndicator(outputIndicator) } returns outputIndicatorDetail
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                outputIndicator.id,
                outputIndicator.identifier
            )
        } returns false
        every { persistence.getCountOfOutputIndicators() } returns 0

        assertThat(createOutputIndicator.createOutputIndicator(outputIndicator))
            .isEqualTo(outputIndicatorDetail)

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_ADDED)
            assertThat(description).isEqualTo("Programme indicator ID01 has been added")
        }
    }

    @Test
    fun `should throw IdentifierIsUsedException when output indicator identifier is already used`() {
        val outputIndicator = buildOutputIndicatorInstance(id = 0L)
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                outputIndicator.id,
                outputIndicator.identifier
            )
        } returns true

        val exception = catchThrowableOfType(
            { createOutputIndicator.createOutputIndicator(outputIndicator) },
            IdentifierIsUsedException::class.java
        )

        assertThat(exception.formErrors["identifier"]).isEqualTo(I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"))
    }

    @Test
    fun `should throw InvalidResultIndicatorException when selected result indicator's specific objective does not match the specific objective of output indicator`() {
        val outputIndicator = buildOutputIndicatorInstance(id = 0L, resultIndicatorId = 2L)
        val resultIndicatorDetail = buildResultIndicatorDetailInstance(
            id = 2L,
            programmeObjectivePolicy = ProgrammeObjectivePolicy.CircularEconomy
        )
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                outputIndicator.id,
                outputIndicator.identifier
            )
        } returns false

        every {
            resultIndicatorPersistence.getResultIndicator(outputIndicator.resultIndicatorId!!)
        } returns resultIndicatorDetail
        val exception = catchThrowableOfType(
            { createOutputIndicator.createOutputIndicator(outputIndicator) },
            InvalidResultIndicatorException::class.java
        )

        assertThat(exception.formErrors["resultIndicatorId"]).isEqualTo(I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.invalid.result.indicator"))
    }

    @Test
    fun `should throw OutputIndicatorsCountExceedException when count of output indicators exceed the limit`() {
        val outputIndicator = buildOutputIndicatorInstance(id = 0L)
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(outputIndicator.id, outputIndicator.identifier)
        } returns false
        every { persistence.getCountOfOutputIndicators() } returns 60

        assertThatExceptionOfType(OutputIndicatorsCountExceedException::class.java).isThrownBy {
            createOutputIndicator.createOutputIndicator(outputIndicator)
        }

    }
}
