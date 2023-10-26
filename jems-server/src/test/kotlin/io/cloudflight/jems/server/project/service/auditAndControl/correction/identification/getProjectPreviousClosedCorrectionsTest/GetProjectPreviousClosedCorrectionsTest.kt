package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectPreviousClosedCorrectionsTest

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification.GetProjectPreviousClosedCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetProjectPreviousClosedCorrectionsTest : UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L

        private val correction = ProjectAuditControlCorrection(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatus.Closed,
            linkedToInvoice = true,
        )
    }

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var getProjectPreviousClosedCorrection: GetProjectPreviousClosedCorrection

    @Test
    fun getProjectPreviousClosedCorrections() {
        every { correctionPersistence.getPreviousClosedCorrections(AUDIT_CONTROL_ID, CORRECTION_ID) } returns listOf(
            correction
        )
        Assertions.assertThat(
            getProjectPreviousClosedCorrection.getProjectPreviousClosedCorrections(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        ).isEqualTo(
            listOf(correction)
        )
    }

}
