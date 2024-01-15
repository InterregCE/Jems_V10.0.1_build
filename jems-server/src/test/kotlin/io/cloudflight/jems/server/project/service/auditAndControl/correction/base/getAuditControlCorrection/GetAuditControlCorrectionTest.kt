package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.getAuditControlCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetAuditControlCorrectionTest: UnitTest() {

    companion object {
        private const val CORRECTION_ID = 1L
    }

    @MockK
    private lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    private lateinit var interactor: GetAuditControlCorrection

    @Test
    fun getProjectCorrectionIdentification() {
        val result = mockk<AuditControlCorrectionDetail>()
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns result
        assertThat(interactor.getCorrection(CORRECTION_ID)).isEqualTo(result)
    }

}
