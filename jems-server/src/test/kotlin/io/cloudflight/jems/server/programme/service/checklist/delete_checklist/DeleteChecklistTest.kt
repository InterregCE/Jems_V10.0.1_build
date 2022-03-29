package io.cloudflight.jems.server.programme.service.checklist.delete_checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.getList.GetProgrammeChecklistDetailNotFoundException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteChecklistTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @InjectMockKs
    lateinit var deleteProgrammeChecklist: DeleteProgrammeChecklist

    @Test
    fun `delete checklist - OK`() {
        every { persistence.deleteById(CHECKLIST_ID) } answers {}
        deleteProgrammeChecklist.deleteProgrammeChecklist(CHECKLIST_ID)
        verify { persistence.deleteById(CHECKLIST_ID) }
    }

    @Test
    fun `delete checklist - not existing`() {
        every { persistence.deleteById(-1L) } throws GetProgrammeChecklistDetailNotFoundException()
        assertThrows<GetProgrammeChecklistDetailNotFoundException> {
            deleteProgrammeChecklist
                .deleteProgrammeChecklist(-1L)
        }
    }
}
