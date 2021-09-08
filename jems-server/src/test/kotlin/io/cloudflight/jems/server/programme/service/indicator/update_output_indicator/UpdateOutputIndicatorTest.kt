package io.cloudflight.jems.server.programme.service.indicator.update_output_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class UpdateOutputIndicatorTest : IndicatorsBaseTest() {


    @MockK
    lateinit var persistence: OutputIndicatorPersistence

    @MockK
    lateinit var resultIndicatorPersistence: ResultIndicatorPersistence

    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var updateOutputIndicator: UpdateOutputIndicator

    @Test
    fun `should update and return output indicator when there is no problem`() {
        val newOutputIndicator = buildOutputIndicatorInstance(code = "new code")
        val oldOutputIndicatorDetail = buildOutputIndicatorDetailInstance()
        val savedOutputIndicatorDetail = buildOutputIndicatorDetailInstance(code = "new code")
        every { persistence.getOutputIndicator(newOutputIndicator.id!!) } returns oldOutputIndicatorDetail
        every { persistence.saveOutputIndicator(newOutputIndicator) } returns savedOutputIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns false
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                newOutputIndicator.id,
                newOutputIndicator.identifier
            )
        } returns false

        assertThat(updateOutputIndicator.updateOutputIndicator(newOutputIndicator))
            .isEqualTo(savedOutputIndicatorDetail)

        val auditLog1 = slot<AuditCandidateEvent>()
        verify { auditPublisher.publishEvent(capture(auditLog1)) }
        with(auditLog1.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description)
                .isEqualTo("Programme indicator ID01 edited:\ncode changed from ioCODE to new code")
        }
    }


    @Test
    fun `should update and return output indicator when output indicator identifier is changed and it's not used by other output indicators`() {
        val newOutputIndicator = buildOutputIndicatorInstance(identifier = "newID")
        val oldOutputIndicatorDetail = buildOutputIndicatorDetailInstance()
        val savedOutputIndicatorDetail = buildOutputIndicatorDetailInstance(identifier = "newID")
        every { persistence.getOutputIndicator(newOutputIndicator.id!!) } returns oldOutputIndicatorDetail
        every { persistence.saveOutputIndicator(newOutputIndicator) } returns savedOutputIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns false
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                newOutputIndicator.id,
                newOutputIndicator.identifier
            )
        } returns false

        assertThat(updateOutputIndicator.updateOutputIndicator(newOutputIndicator))
            .isEqualTo(savedOutputIndicatorDetail)

        val auditLog = slot<AuditCandidateEvent>()
        verify { auditPublisher.publishEvent(capture(auditLog)) }
        with(auditLog.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description)
                .isEqualTo("Programme indicator newID edited:\nidentifier changed from ID01 to newID")
        }
    }

    @Test
    fun `should throw IdentifierIsUsedException when new output indicator identifier is already used by another output indicator`() {
        val newOutputIndicator = buildOutputIndicatorInstance(identifier = "newId")
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                newOutputIndicator.id!!,
                newOutputIndicator.identifier
            )
        } returns true

        val exception = catchThrowableOfType(
            { updateOutputIndicator.updateOutputIndicator(newOutputIndicator) },
            IdentifierIsUsedException::class.java
        )

        assertThat(exception.formErrors["identifier"]).isEqualTo(I18nMessage("use.case.update.output.indicator.identifier.is.used"))

    }

    @Test
    fun `should throw InvalidResultIndicatorException when selected result indicator's specific objective does not match the specific objective of output indicator`() {
        val outputIndicator = buildOutputIndicatorInstance(resultIndicatorId = 2L)
        val resultIndicatorDetail = buildResultIndicatorDetailInstance(id = 2L, programmeObjectivePolicy = ProgrammeObjectivePolicy.CircularEconomy)
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
            { updateOutputIndicator.updateOutputIndicator(outputIndicator) },
            InvalidResultIndicatorException::class.java
        )

        assertThat(exception.formErrors["resultIndicatorId"]).isEqualTo(I18nMessage("use.case.update.output.indicator.invalid.result.indicator"))
    }

    @Test
    fun `should throw Exception when specific objective in the new output indicator is changed and there is a published call`() {
        val newOutputIndicator = buildOutputIndicatorInstance(programmeObjectivePolicy = ProgrammeObjectivePolicy.Digitisation)
        val oldOutputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every { persistence.getOutputIndicator(newOutputIndicator.id!!) } returns oldOutputIndicatorDetail
        every { callPersistence.hasAnyCallPublished() } returns true
        every {
            persistence.isIdentifierUsedByAnotherOutputIndicator(
                newOutputIndicator.id,
                newOutputIndicator.identifier
            )
        } returns false
        val ex = assertThrows<OutputIndicatorCannotBeChangedAfterCallIsPublished> {
            updateOutputIndicator.updateOutputIndicator(newOutputIndicator)
        }
        assertThat(ex.formErrors.keys).containsExactly("specificObjective")
    }
}
