package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.Test

class GetModificationDecisionsInteractorTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 5L
    }

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @RelaxedMockK
    lateinit var projectWorkflowPersistence: ProjectWorkflowPersistence

    @InjectMockKs
    lateinit var getModificationDecision: GetModificationDecision

    @Test
    fun getModificationDecisions() {
        every { correctionPersistence.getCorrectionsForModificationDecisions(PROJECT_ID) } returns emptyMap()

        getModificationDecision.getModificationDecisions(PROJECT_ID)
        verify(exactly = 1) { projectWorkflowPersistence.getModificationDecisions(PROJECT_ID) }
    }
}
