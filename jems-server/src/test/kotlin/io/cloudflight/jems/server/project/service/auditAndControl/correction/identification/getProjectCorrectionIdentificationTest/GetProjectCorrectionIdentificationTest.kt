package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentificationTest

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification.GetProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class GetProjectCorrectionIdentificationTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val CORRECTION_ID = 1L
        private val zonedDateNow = ZonedDateTime.now()
        private val zonedDateTomorrow = ZonedDateTime.now().plusDays(1)

        private val correction = ProjectAuditControlCorrection(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val correctionIdentification = ProjectCorrectionIdentification(
            correction = correction,
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = 1L,
            partnerReportId = 3L,
            programmeFundId = 14L
        )
    }

    @MockK
    lateinit var correctionIdentificationPersistence: ProjectCorrectionIdentificationPersistence

    @InjectMockKs
    lateinit var getProjectCorrectionIdentification: GetProjectCorrectionIdentification

    @Test
    fun getProjectCorrectionIdentification() {
        every { correctionIdentificationPersistence.getCorrectionIdentification(CORRECTION_ID) } returns correctionIdentification
        assertThat(getProjectCorrectionIdentification.getProjectCorrectionIdentification(CORRECTION_ID)).isEqualTo(
            correctionIdentification)
    }

}
