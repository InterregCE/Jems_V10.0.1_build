package io.cloudflight.jems.server.programme.service.indicator.update_result_indicator

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

internal class UpdateResultIndicatorTest : IndicatorsBaseTest() {


    @MockK
    lateinit var persistence: ResultIndicatorPersistence

    @InjectMockKs
    lateinit var updateResultIndicator: UpdateResultIndicator

    @Test
    fun `should update and return result indicator when there is no problem`() {
        val newResultIndicator = buildResultIndicatorInstance(measurementUnit = "new measurement unit")
        val oldResultIndicatorDetail = buildResultIndicatorDetailInstance()
        val savedResultIndicatorDetail = buildResultIndicatorDetailInstance(measurementUnit = "new measurement unit")
        every { persistence.getResultIndicator(newResultIndicator.id!!) } returns oldResultIndicatorDetail
        every { persistence.saveResultIndicator(newResultIndicator) } returns savedResultIndicatorDetail
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id,
                newResultIndicator.identifier
            )
        } returns false

        assertThat(updateResultIndicator.updateResultIndicator(newResultIndicator))
            .isEqualTo(savedResultIndicatorDetail)

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description)
                .isEqualTo("Programme indicator ID01 edited:\nmeasurementUnit changed from measurement unit to new measurement unit")
        }
    }


    @Test
    fun `should update and return result indicator when result indicator identifier is changed and it's not used by other result indicators`() {
        val newResultIndicator = buildResultIndicatorInstance(identifier = "newID")
        val oldResultIndicatorDetail = buildResultIndicatorDetailInstance()
        val savedResultIndicatorDetail = buildResultIndicatorDetailInstance(identifier = "newID")
        every { persistence.getResultIndicator(newResultIndicator.id!!) } returns oldResultIndicatorDetail
        every { persistence.saveResultIndicator(newResultIndicator) } returns savedResultIndicatorDetail
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id,
                newResultIndicator.identifier
            )
        } returns false

        assertThat(updateResultIndicator.updateResultIndicator(newResultIndicator))
            .isEqualTo(savedResultIndicatorDetail)

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description)
                .isEqualTo("Programme indicator newID edited:\nidentifier changed from ID01 to newID")
        }
    }

    @Test
    fun `should throw IdentifierIsUsedException when new result indicator identifier is already used by another result indicator`() {
        val newResultIndicator = buildResultIndicatorInstance(identifier = "newId")
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id!!,
                newResultIndicator.identifier
            )
        } returns true

        val exception = catchThrowableOfType(
            { updateResultIndicator.updateResultIndicator(newResultIndicator) },
            IdentifierIsUsedException::class.java
        )

        assertThat(exception.formErrors["identifier"]).isEqualTo(I18nMessage("$UPDATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"))
    }

    @Test
    fun `should throw InvalidIdException when new result indicator id is zero`() {
        val newResultIndicator = buildResultIndicatorInstance(id = 0L)
        assertThatExceptionOfType(InvalidIdException::class.java).isThrownBy {
            updateResultIndicator.updateResultIndicator(newResultIndicator)
        }
    }
}
