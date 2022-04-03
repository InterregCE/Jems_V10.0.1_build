package io.cloudflight.jems.server.programme.service.checklist.get_detail_checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.getDetail.GetProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

internal class GetChecklistDetailTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    private val checklist = ProgrammeChecklistDetail(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistType.ELIGIBILITY,
        name = "name",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        components = emptyList()
    )

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @InjectMockKs
    lateinit var getProgrammeChecklist: GetProgrammeChecklistDetail

    @Test
    fun getChecklistDetail() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checklist
        assertThat(getProgrammeChecklist.getProgrammeChecklistDetail(CHECKLIST_ID))
            .usingRecursiveComparison()
            .isEqualTo(checklist)
    }

}
