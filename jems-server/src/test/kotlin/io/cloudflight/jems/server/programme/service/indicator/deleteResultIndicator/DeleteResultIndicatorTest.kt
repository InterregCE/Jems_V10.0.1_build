package io.cloudflight.jems.server.programme.service.indicator.deleteResultIndicator

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteResultIndicatorTest : IndicatorsBaseTest() {

    @MockK
    lateinit var persistence: ResultIndicatorPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @InjectMockKs
    lateinit var deleteResultIndicator: DeleteResultIndicator

    @Test
    fun `should successfully delete result indicator`() {
        val savedResultIndicator = buildResultIndicatorDetailInstance()
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getResultIndicator(1L) } returns savedResultIndicator
        every { persistence.deleteResultIndicator(1L) } returns Unit
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        deleteResultIndicator.deleteResultIndicator(1L)

        verify { persistence.deleteResultIndicator(1L) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROGRAMME_INDICATOR_DELETED,
                description = "Programme indicator ${savedResultIndicator.identifier} has been deleted"
            )
        )
    }

    @Test
    fun `should throw Exception on result indicator delete in use`() {
        every { isProgrammeSetupLocked.isLocked() } returns true

        val ex = assertThrows<DeleteResultIndicatorProgrammeSetupRestrictedException> {
            deleteResultIndicator.deleteResultIndicator(2L)
        }
        assertThat(ex.i18nMessage).isEqualTo(
            I18nMessage("use.case.delete.result.indicator.programme.setup.restricted")
        )
    }
}
