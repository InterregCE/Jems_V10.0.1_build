package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl.GetPartnerAndPartnerReportDataService
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class UpdateAuditControlCorrectionTest : UnitTest() {

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var allowedDataService: GetPartnerAndPartnerReportDataService

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

        every { allowedDataService.getPartnerAndPartnerReportData(150L) } returns listOf(
            CorrectionAvailablePartner(-1L, -2, "", mockk(), false,
                availableReports = listOf(
                    CorrectionAvailablePartnerReport(id = reportId, -3, null,
                        availableReportFunds = listOf(ProgrammeFund(fundId, true, ProgrammeFundType.ERDF)),
                        availablePayments = emptyList(),
                    ),
                ),
            )
        )

        val toUpdate = AuditControlCorrectionUpdate(
            followUpOfCorrectionId = 333L,
            correctionFollowUpType = CorrectionFollowUpType.Interest,
            repaymentFrom = LocalDate.now().minusDays(1),
            lateRepaymentTo = LocalDate.now().plusDays(1),
            partnerReportId = reportId,
            programmeFundId = fundId,
        )
        val result = mockk<AuditControlCorrectionDetail>()
        every { auditControlCorrectionPersistence.updateCorrection(14L, toUpdate) } returns result

        assertThat(interactor.updateCorrection(14L, toUpdate)).isEqualTo(result)
    }

    @Test
    fun `updateCorrection - invalid report data`() {
        // TODO
    }

    @Test
    fun `updateCorrection - audit control is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(16L) } returns mockk {
            every { auditControlId } returns 476L
            every { status } returns AuditControlStatus.Ongoing
        }
        every { auditControlPersistence.getById(476L) } returns mockk {
            every { status } returns AuditControlStatus.Closed
        }

        assertThrows<AuditControlClosedException> { interactor.updateCorrection(16L, mockk()) }
        verify(exactly = 0) { auditControlCorrectionPersistence.updateCorrection(any(), any()) }
    }

    @Test
    fun `updateCorrection - correction is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(14L) } returns mockk {
            every { auditControlId } returns 475L
            every { status } returns AuditControlStatus.Closed
        }
        every { auditControlPersistence.getById(475L) } returns mockk {
            every { status } returns AuditControlStatus.Ongoing
        }

        assertThrows<AuditControlCorrectionClosedException> { interactor.updateCorrection(14L, mockk()) }
        verify(exactly = 0) { auditControlCorrectionPersistence.updateCorrection(any(), any()) }
    }
}
