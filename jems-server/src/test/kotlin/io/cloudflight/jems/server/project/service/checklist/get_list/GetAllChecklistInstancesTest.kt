package io.cloudflight.jems.server.project.service.checklist.get_list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getAllInstances.GetAllChecklistInstances
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class GetAllChecklistInstancesTest : UnitTest() {

    @RelaxedMockK
    lateinit var persistence: ChecklistInstancePersistence

    @RelaxedMockK
    lateinit var checklistAuthorization: ProjectChecklistAuthorization

    @InjectMockKs
    lateinit var getAllChecklistInstances: GetAllChecklistInstances

    @Test
    fun `get all instances`() {
        every { checklistAuthorization.canConsolidate(1) } returns true

        getAllChecklistInstances.getChecklistInstancesByTypeAndRelatedId(1, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT)

        verify { persistence.getChecklistsByRelatedIdAndType(1, ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT) }
    }
}
