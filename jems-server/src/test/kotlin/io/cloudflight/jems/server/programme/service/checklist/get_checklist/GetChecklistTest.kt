package io.cloudflight.jems.server.programme.service.checklist.get_checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

internal class GetChecklistTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    private val checklist = ProgrammeChecklist(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false
    )

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @InjectMockKs
    lateinit var getProgrammeChecklist: GetProgrammeChecklist

    @Test
    fun getMax100Checklists() {
        every { persistence.getMax100Checklists() } returns listOf(checklist)
        assertThat(getProgrammeChecklist.getProgrammeChecklist()).containsExactly(checklist)
    }

}
