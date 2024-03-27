package io.cloudflight.jems.server.programme.service.checklist.get_checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

internal class GetChecklistTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    private val checklist = ProgrammeChecklist(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false
    )

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var getProgrammeChecklist: GetProgrammeChecklist

    @Test
    fun getProgrammeChecklist() {
        every { persistence.getMax100Checklists(Sort.unsorted()) } returns listOf(checklist)
        assertThat(getProgrammeChecklist.getProgrammeChecklist(Sort.unsorted())).containsExactly(checklist)
    }

    @Test
    fun getProgrammeChecklistsByType() {
        every { persistence.getChecklistsByTypeAndCall(ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT, 10L) } returns
                listOf(IdNamePair(checklist.id!!, checklist.name!!))
        every { projectPersistence.getProjectSummary(projectId = 20L) } returns mockk { every { callId } returns 10L }
        assertThat(
            getProgrammeChecklist.getProgrammeChecklistsByType(
                checklistType = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
                projectId = 20L,
            )
        ).containsExactly(IdNamePair(checklist.id!!, checklist.name!!))
    }

}
