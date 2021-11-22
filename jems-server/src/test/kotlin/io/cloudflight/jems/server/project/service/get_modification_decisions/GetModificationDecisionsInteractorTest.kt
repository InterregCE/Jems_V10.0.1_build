package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.Test

class GetModificationDecisionsInteractorTest: UnitTest() {

    companion object {
        private const val projectId = 5L
    }

    @RelaxedMockK
    lateinit var projectWorkflowPersistence: ProjectWorkflowPersistence

    @InjectMockKs
    lateinit var getModificationDecision: GetModificationDecision

    @Test
    fun getModificationDecisions() {
        getModificationDecision.getModificationDecisions(projectId)
        verify(exactly = 1) { projectWorkflowPersistence.getModificationDecisions(projectId) }
    }
}
