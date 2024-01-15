package io.cloudflight.jems.server.programme.service.checklist.clone

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

class CloneProgrammeChecklistTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    private val checkList = ProgrammeChecklistDetail(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
        components = emptyList()
    )

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var cloneProgrammeChecklist: CloneProgrammeChecklist

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
    }

    @Test
    fun `clone - successfully`() {
        val newChecklist = checkList.copy(id = null, name = "name - COPY")
        val copiedChecklist = checkList.copy(id = CHECKLIST_ID + 1, name = "name - COPY")

        every { persistence.countAll() } returns 1
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkList
        every { persistence.createChecklist(newChecklist) } returns copiedChecklist

        Assertions.assertThat(cloneProgrammeChecklist.clone(CHECKLIST_ID)).isEqualTo(copiedChecklist)
    }

    @Test
    fun `clone - max amount reached`() {
        every { persistence.countAll() } returns 101
        assertThrows<MaxAmountOfProgrammeChecklistReached> { cloneProgrammeChecklist.clone(CHECKLIST_ID) }
    }
}
