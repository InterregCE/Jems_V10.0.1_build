package io.cloudflight.jems.server.programme.service.indicator.deleteOutputIndicator

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteOutputIndicatorTest : IndicatorsBaseTest() {

    @MockK
    lateinit var persistence: OutputIndicatorPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @InjectMockKs
    lateinit var deleteOutputIndicator: DeleteOutputIndicator

    @Test
    fun `should successfully delete output indicator`() {
        val savedOutputIndicator = buildOutputIndicatorDetailInstance()
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getOutputIndicator(1L) } returns savedOutputIndicator
        every { persistence.deleteOutputIndicator(1L) } returns Unit
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        deleteOutputIndicator.deleteOutputIndicator(1L)

        verify { persistence.deleteOutputIndicator(1L) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROGRAMME_INDICATOR_DELETED,
                description = "Programme indicator ${savedOutputIndicator.identifier} has been deleted"
            )
        )
    }

    @Test
    fun `should throw Exception on output indicator delete in use`() {
        every { isProgrammeSetupLocked.isLocked() } returns true

        val ex = assertThrows<OutputIndicatorDeletionWhenProgrammeSetupRestricted> {
            deleteOutputIndicator.deleteOutputIndicator(2L)
        }
        assertThat(ex.i18nMessage).isEqualTo(
            I18nMessage("use.case.delete.output.indicator.programme.setup.restricted")
        )
    }
}
