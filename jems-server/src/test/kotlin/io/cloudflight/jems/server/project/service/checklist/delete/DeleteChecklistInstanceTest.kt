package io.cloudflight.jems.server.project.service.checklist.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.getList.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @InjectMockKs
    lateinit var deleteChecklistInstance: DeleteChecklistInstance

    @Test
    fun `delete checklist - OK`() {
        every { persistence.deleteById(CHECKLIST_ID) } answers {}
        deleteChecklistInstance.deleteById(CHECKLIST_ID)
        verify { persistence.deleteById(CHECKLIST_ID) }
    }

    @Test
    fun `delete checklist - not existing`() {
        every { persistence.deleteById(-1L) } throws GetChecklistInstanceDetailNotFoundException()
        assertThrows<GetChecklistInstanceDetailNotFoundException> {
            deleteChecklistInstance.deleteById(-1L)
        }
    }
}
