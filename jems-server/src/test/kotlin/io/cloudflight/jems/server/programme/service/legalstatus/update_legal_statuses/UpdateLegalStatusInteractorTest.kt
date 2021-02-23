package io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditCandidateWithUser
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateLegalStatusInteractorTest : UnitTest() {

    companion object {
        private val alreadyExistingLegalStatus = ProgrammeLegalStatus(
            id = 14,
            translatedValues = setOf(
                ProgrammeLegalStatusTranslatedValue(language = EN, description = "EN desc"),
                ProgrammeLegalStatusTranslatedValue(language = SK, description = "SK desc"),
            )
        )
    }

    @MockK
    lateinit var persistence: ProgrammeLegalStatusPersistence

    @RelaxedMockK
    lateinit var auditService: AuditService

    @InjectMockKs
    lateinit var updateLegalStatus: UpdateLegalStatus

    @MockK
    lateinit var mockedList: List<ProgrammeLegalStatus>

    @Test
    fun `update legal statuses - everything should be fine`() {
        val legalStatus = ProgrammeLegalStatus(translatedValues = setOf(
            ProgrammeLegalStatusTranslatedValue(language = EN, description = "already existing legal status")
        ))

        val slotToDeleteIds = slot<Set<Long>>()
        val slotLegalStatuses = slot<Collection<ProgrammeLegalStatus>>()
        every { persistence.isProgrammeSetupRestricted() } returns false
        every { persistence.updateLegalStatuses(capture(slotToDeleteIds), capture(slotLegalStatuses)) } returns
            listOf(alreadyExistingLegalStatus, legalStatus.copy(id = 30))

        assertThat(updateLegalStatus.updateLegalStatuses(
            toDeleteIds = setOf(22, 25),
            toPersist = listOf(legalStatus)
        )).containsExactly(
            alreadyExistingLegalStatus, legalStatus.copy(id = 30)
        )

        assertThat(slotToDeleteIds.captured).containsExactly(22, 25)
        assertThat(slotLegalStatuses.captured).containsExactly(legalStatus)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event.captured) {
            assertThat(action).isEqualTo(AuditAction.LEGAL_STATUS_EDITED)
            assertThat(description).isEqualTo("Values for partner legal status set to:\n" +
                "[EN=EN desc, SK=SK desc],\n" +
                "[EN=already existing legal status]"
            )
        }
    }

    @Test
    fun `update legal statuses - when programme setup is restricted we cannot delete`() {
        every { persistence.isProgrammeSetupRestricted() } returns true
        assertThrows<DeletionWhenProgrammeSetupRestricted> { updateLegalStatus.updateLegalStatuses(setOf(-1), emptySet()) }
    }

    @Test
    fun `update legal statuses - when programme setup description is longer then allowed exception should be thrown`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
        assertThrows<LegalStatusesDescriptionTooLong> { updateLegalStatus.updateLegalStatuses(emptySet(), listOf(
            ProgrammeLegalStatus(translatedValues = setOf(ProgrammeLegalStatusTranslatedValue(
                language = EN,
                description = getStringOfLength(128),
            )))
        )) }
    }

    @Test
    fun `update legal statuses - exception should be thrown when more legal statuses would be created then allowed`() {
        every { mockedList.size } returns 21
        every { persistence.isProgrammeSetupRestricted() } returns true
        every { persistence.updateLegalStatuses(any(), any()) } returns mockedList

        val ex = assertThrows<MaxAllowedLegalStatusesReachedException> { updateLegalStatus.updateLegalStatuses(emptySet(), emptyList()) }
        assertThat(ex.message).isEqualTo("max allowed: 20")

        verify(exactly = 0) { auditService.logEvent(any<AuditCandidate>()) }
        verify(exactly = 0) { auditService.logEvent(any<AuditCandidateWithUser>()) }
    }

}
