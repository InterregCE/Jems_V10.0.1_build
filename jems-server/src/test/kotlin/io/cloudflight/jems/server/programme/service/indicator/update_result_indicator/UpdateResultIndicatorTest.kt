package io.cloudflight.jems.server.programme.service.indicator.update_result_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
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
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class UpdateResultIndicatorTest : IndicatorsBaseTest() {


    @MockK
    lateinit var persistence: ResultIndicatorPersistence

    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var updateResultIndicator: UpdateResultIndicator

    @Test
    fun `should update and return result indicator when there is no problem`() {
        val newResultIndicator = buildResultIndicatorInstance(code = "new code")
        val oldResultIndicatorDetail = buildResultIndicatorDetailInstance()
        val savedResultIndicatorDetail = buildResultIndicatorDetailInstance(code = "new code")
        every { persistence.getResultIndicator(newResultIndicator.id!!) } returns oldResultIndicatorDetail
        every { persistence.saveResultIndicator(newResultIndicator) } returns savedResultIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns false
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id,
                newResultIndicator.identifier
            )
        } returns false

        assertThat(updateResultIndicator.updateResultIndicator(newResultIndicator))
            .isEqualTo(savedResultIndicatorDetail)

        val auditLog = slot<AuditCandidateEvent>()
        verify { auditPublisher.publishEvent(capture(auditLog)) }
        with(auditLog.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description)
                .isEqualTo("Programme indicator ID01 edited:\ncode changed from ioCODE to new code")
        }
    }


    @Test
    fun `should update and return result indicator when result indicator identifier is changed and it's not used by other result indicators`() {
        val newResultIndicator = buildResultIndicatorInstance(identifier = "newID")
        val oldResultIndicatorDetail = buildResultIndicatorDetailInstance()
        val savedResultIndicatorDetail = buildResultIndicatorDetailInstance(identifier = "newID")
        every { persistence.getResultIndicator(newResultIndicator.id!!) } returns oldResultIndicatorDetail
        every { persistence.saveResultIndicator(newResultIndicator) } returns savedResultIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns false
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id,
                newResultIndicator.identifier
            )
        } returns false

        assertThat(updateResultIndicator.updateResultIndicator(newResultIndicator))
            .isEqualTo(savedResultIndicatorDetail)

        val auditLog = slot<AuditCandidateEvent>()
        verify { auditPublisher.publishEvent(capture(auditLog)) }
        with(auditLog.captured.auditCandidate) {
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
    fun `should throw Exception when specific objective in the new result indicator is changed and there is a published call`() {
        val newResultIndicator = buildResultIndicatorInstance(programmeObjectivePolicy = ProgrammeObjectivePolicy.Digitisation)
        val oldResultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { persistence.getResultIndicator(newResultIndicator.id!!) } returns oldResultIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns true
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id,
                newResultIndicator.identifier
            )
        } returns false
        val ex = assertThrows<ResultIndicatorCannotBeChangedAfterCallIsPublished> {
            updateResultIndicator.updateResultIndicator(newResultIndicator)
        }
        assertThat(ex.formErrors.keys).containsExactly("specificObjective")
    }

    @Test
    fun `should throw Exception when baseline in the new result indicator is decreased and there is a published call`() {
        val newResultIndicator = buildResultIndicatorInstance(baseline = BigDecimal.ONE)
        val oldResultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { persistence.getResultIndicator(newResultIndicator.id!!) } returns oldResultIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns true
        every {
            persistence.isIdentifierUsedByAnotherResultIndicator(
                newResultIndicator.id,
                newResultIndicator.identifier
            )
        } returns false
        val ex = assertThrows<ResultIndicatorCannotBeChangedAfterCallIsPublished> {
            updateResultIndicator.updateResultIndicator(newResultIndicator)
        }
        assertThat(ex.formErrors.keys).containsExactly("baseline")
    }
}
