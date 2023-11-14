package io.cloudflight.jems.server.project.service.auditAndControl.correction.listPreviouslyClosedCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListPreviouslyClosedCorrectionTest : UnitTest() {

    @MockK
    private lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    private lateinit var interactor: ListPreviouslyClosedCorrection

    @Test
    fun getProjectPreviousClosedCorrections() {
        val result = mockk<List<AuditControlCorrection>>()
        every { correctionPersistence.getPreviousClosedCorrections(78L) } returns result
        assertThat(interactor.getClosedCorrectionsBefore(78L)).isEqualTo(result)
    }

}
