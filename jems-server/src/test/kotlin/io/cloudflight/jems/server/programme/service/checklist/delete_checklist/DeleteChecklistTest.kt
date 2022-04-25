package io.cloudflight.jems.server.programme.service.checklist.delete_checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.update.ChecklistLockedException
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DeleteChecklistTest : UnitTest() {

    companion object {

        private const val CHECKLIST_ID = 100L

        private fun getChecklist(locked: Boolean = false) = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            locked = locked,
            components = emptyList()
        )
    }

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @MockK
    lateinit var checklistInstancePersistence: ChecklistInstancePersistence

    @InjectMockKs
    lateinit var deleteProgrammeChecklist: DeleteProgrammeChecklist

    @Test
    fun `delete checklist - OK`() {
        val checklist = getChecklist()
        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 0
        every { persistence.deleteById(CHECKLIST_ID) } returns Unit
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checklist
        deleteProgrammeChecklist.deleteProgrammeChecklist(CHECKLIST_ID)
        verify { persistence.deleteById(CHECKLIST_ID) }
    }

    @Test
    fun `delete checklist - not existing`() {
        val idNotExisting = -1L
        every { checklistInstancePersistence.countAllByChecklistTemplateId(idNotExisting) } returns 0
        every { persistence.deleteById(idNotExisting) } throws GetProgrammeChecklistDetailNotFoundException()
        assertThrows<GetProgrammeChecklistDetailNotFoundException> {
            deleteProgrammeChecklist
                .deleteProgrammeChecklist(idNotExisting)
        }
    }

    @Test
    fun `delete checklist - locked`() {
        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 1
        assertThrows<ChecklistLockedException> { deleteProgrammeChecklist.deleteProgrammeChecklist(CHECKLIST_ID) }
    }

}
