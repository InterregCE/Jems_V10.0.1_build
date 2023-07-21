package io.cloudflight.jems.server.programme.service.costoption.deleteLumpSum

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.PaymentClaim
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class DeleteLumpSumInteractorTest {

    companion object {
        private const val lumpSumId = 1L
        private val lumpSum = ProgrammeLumpSum(
            id = lumpSumId,
            name = setOf(InputTranslation(SystemLanguage.EN, "LumpSum")),
            fastTrack = false,
            splittingAllowed = false,
            paymentClaim = PaymentClaim.IncurredByBeneficiaries
        )
    }

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    private lateinit var deleteLumpSumInteractor: DeleteLumpSumInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        deleteLumpSumInteractor = DeleteLumpSum(persistence, isProgrammeSetupLocked, auditPublisher)
    }

    @Test
    fun `delete lump sum - OK`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(lumpSumId) } returns 0
        every { persistence.getLumpSum(lumpSumId) } returns lumpSum
        every { persistence.deleteLumpSum(lumpSumId) } returns Unit
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        deleteLumpSumInteractor.deleteLumpSum(lumpSumId)
        verify { persistence.deleteLumpSum(lumpSumId) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROGRAMME_LUMP_SUM_DELETED,
                description = "Programme lump sum (id=${lumpSumId}) '${lumpSum.name}' has been deleted"
            )
        )
    }

    @Test
    fun `delete lump sum - not existing`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(-1L) } returns 0
        every { persistence.getLumpSum(-1L) } throws ResourceNotFoundException("programmeLumpSum")
        assertThrows<ResourceNotFoundException> { deleteLumpSumInteractor.deleteLumpSum(-1L) }
    }

    @Test
    fun `delete lump sum - deletion error`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(-1L) } returns 0
        every { persistence.getLumpSum(-1L) } returns lumpSum
        every { persistence.deleteLumpSum(-1L) } throws ResourceNotFoundException("programmeLumpSum")
        assertThrows<ResourceNotFoundException> { deleteLumpSumInteractor.deleteLumpSum(-1L) }
    }

    @Test
    fun `delete lump sum - already in use`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(1L) } returns 1
        assertThrows<ToDeleteLumpSumAlreadyUsedInCall> { deleteLumpSumInteractor.deleteLumpSum(1L) }
    }

    @Test
    fun `delete lump sum - call already published`() {
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeleteLumpSumWhenProgrammeSetupRestricted> { deleteLumpSumInteractor.deleteLumpSum(1L) }
    }

}
