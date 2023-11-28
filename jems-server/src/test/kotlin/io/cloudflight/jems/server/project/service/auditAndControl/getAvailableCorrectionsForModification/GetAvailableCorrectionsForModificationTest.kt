package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForModification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetAvailableCorrectionsForModificationTest : UnitTest() {

    @MockK
    lateinit var correctionsPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableCorrectionsForModification

    @Test
    fun getAvailableCorrections() {
        val corrections = listOf(mockk<AuditControlCorrection>())
        every { correctionsPersistence.getAvailableCorrectionsForModification(1L) } returns corrections

        assertThat(interactor.getAvailableCorrections(1L)).isEqualTo(corrections)
    }
}
