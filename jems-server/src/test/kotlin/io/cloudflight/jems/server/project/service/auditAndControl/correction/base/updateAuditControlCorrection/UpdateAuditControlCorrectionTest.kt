package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UpdateAuditControlCorrectionTest : UnitTest() {

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var correctionValidator: CorrectionIdentificationValidator

    @InjectMockKs
    private lateinit var interactor: UpdateAuditControlCorrection

    @Test
    fun updateCorrection() {
        val reportId = 847L
        val fundId = 317L
        every { auditControlCorrectionPersistence.getByCorrectionId(14L) } returns mockk {
            every { auditControlId } returns 474L
            every { status } returns AuditControlStatus.Ongoing
        }
        every { auditControlPersistence.getById(474L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 150L
        }



        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerReportId = reportId,
            programmeFundId = fundId,
            costCategory = null,
            procurementId = null,
            expenditureId = null
        )
        every { correctionValidator.validate(14L, toUpdate) } returns Unit

        val result = mockk<AuditControlCorrectionDetail>()
        every { auditControlCorrectionPersistence.updateCorrection(14L, toUpdate) } returns result

        assertThat(interactor.updateCorrection(14L, toUpdate)).isEqualTo(result)
    }

    @Test
    fun `updateCorrection - invalid report data`() {
        // TODO
    }


}
