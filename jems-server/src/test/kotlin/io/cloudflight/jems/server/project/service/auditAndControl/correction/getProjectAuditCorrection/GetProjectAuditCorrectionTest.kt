package io.cloudflight.jems.server.project.service.auditAndControl.correction.getProjectAuditCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetProjectAuditCorrectionTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val CORRECTION_ID = 3L

        private val correction = ProjectAuditControlCorrection(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 2,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val extendedCorrection = ProjectAuditControlCorrectionExtended(
            correction = correction,
            auditControlNumber = 2,
            projectCustomIdentifier = "0123"
        )

    }

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var getProjectAuditControlCorrection: GetProjectAuditControlCorrection

    @Test
    fun `getProjectAuditCorrectionTest - return correct correction`() {
        every { correctionPersistence.getExtendedByCorrectionId(correctionId = CORRECTION_ID) } returns extendedCorrection
        assertThat(getProjectAuditControlCorrection.getProjectAuditCorrection(CORRECTION_ID)).isEqualTo(extendedCorrection)
    }
}
