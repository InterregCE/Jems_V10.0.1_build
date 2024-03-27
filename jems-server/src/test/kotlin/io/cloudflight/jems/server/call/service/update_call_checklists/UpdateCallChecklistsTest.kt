package io.cloudflight.jems.server.call.service.update_call_checklists

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallChecklist
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class UpdateCallChecklistsTest : UnitTest() {

    companion object {
        const val CALL_ID = 1L
        val SELECTED_CHECKLIST_IDS = setOf(1L, 2L)

        val EXISTING_CHECKLISTS = listOf(
            CallChecklist(
                id = 1,
                name = "Checklist 1",
                type = ProgrammeChecklistType.CONTROL,
                lastModificationDate = null,
                selected = true
            )
        )

        val SELECTED_CHECKLISTS = listOf(
            CallChecklist(
                id = 1,
                name = "Checklist 1",
                type = ProgrammeChecklistType.CONTROL,
                lastModificationDate = null,
                selected = true
            ),
            CallChecklist(
                id = 2,
                name = "Checklist 2",
                type = ProgrammeChecklistType.CONTROL,
                lastModificationDate = null,
                selected = true
            )
        )

        private fun callWithStatus(id: Long, status: CallStatus) = CallDetail(
            id = id,
            name = "",
            status = status,
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
            lengthOfPeriod = 7,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            reportPartnerCheckPluginKey = null,
            controlReportPartnerCheckPluginKey = null,
            reportProjectCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
            controlReportSamplingCheckPluginKey = null
        )
    }

    @MockK
    private lateinit var persistence: CallPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var updateCallChecklists: UpdateCallChecklists

    @Test
    fun `update checklists of unpublished call - ok`() {
        val slotAudit = slot<AuditCandidateEvent>()
        val call = callWithStatus(1L, CallStatus.DRAFT)
        every { persistence.getCallChecklists(CALL_ID, any()) } returns EXISTING_CHECKLISTS
        every { persistence.getCallById(CALL_ID) } returns call
        every { persistence.updateCallChecklistSelection(CALL_ID, SELECTED_CHECKLIST_IDS) } returns SELECTED_CHECKLISTS
        every { auditPublisher.publishEvent(capture(slotAudit)) } just runs

        updateCallChecklists.updateCallChecklists(CALL_ID, SELECTED_CHECKLIST_IDS)

        assertThat(slotAudit.isCaptured).isFalse()
    }

    @Test
    fun `update checklists of published call - ok`() {
        val slotAudit = slot<AuditCandidateEvent>()
        val call = callWithStatus(1L, CallStatus.DRAFT)
        every { persistence.getCallChecklists(CALL_ID, any()) } returns EXISTING_CHECKLISTS
        every { persistence.getCallById(CALL_ID) } returns call
        every { persistence.updateCallChecklistSelection(CALL_ID, SELECTED_CHECKLIST_IDS) } returns SELECTED_CHECKLISTS
        every { auditPublisher.publishEvent(capture(slotAudit)) } just runs

        updateCallChecklists.updateCallChecklists(CALL_ID, SELECTED_CHECKLIST_IDS)

        assertThat(slotAudit.isNull).isFalse()
    }

    @Test
    fun `update checklists of published call - no change`() {
        val slotAudit = slot<AuditCandidateEvent>()
        val call = callWithStatus(1L, CallStatus.DRAFT)
        every { persistence.getCallChecklists(CALL_ID, any()) } returns EXISTING_CHECKLISTS
        every { persistence.getCallById(CALL_ID) } returns call
        every { persistence.updateCallChecklistSelection(CALL_ID, setOf(1L)) } returns SELECTED_CHECKLISTS
        every { auditPublisher.publishEvent(capture(slotAudit)) } just runs

        updateCallChecklists.updateCallChecklists(CALL_ID, SELECTED_CHECKLIST_IDS)

        assertThat(slotAudit.isCaptured).isFalse()
    }
}
