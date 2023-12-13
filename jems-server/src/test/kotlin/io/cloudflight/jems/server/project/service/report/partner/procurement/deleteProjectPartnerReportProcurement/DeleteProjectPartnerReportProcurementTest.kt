package io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class DeleteProjectPartnerReportProcurementTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 7544L
    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectPartnerReportProcurementPersistence

    @MockK
    lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var expenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @InjectMockKs
    lateinit var interactor: DeleteProjectPartnerReportProcurement

    @BeforeEach
    fun setup() {
        clearMocks(reportProcurementPersistence, auditControlCorrectionPersistence, expenditurePersistence)
    }

    @ParameterizedTest(name = "delete (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun delete(status: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 7L) } returns
                ProjectPartnerReportStatusAndVersion(reportId = 7L, status, "4.5.8")
        every { reportProcurementPersistence.deletePartnerReportProcurement(PARTNER_ID, reportId = 7L, 587L) } answers { }
        every { expenditurePersistence.existsByProcurementId(587L) } returns false
        every { auditControlCorrectionPersistence.existsByProcurementId(587L) } returns false

        interactor.delete(PARTNER_ID, reportId = 7L, procurementId = 587L)
        verify(exactly = 1) {
            reportProcurementPersistence.deletePartnerReportProcurement(PARTNER_ID, reportId = 7L, procurementId = 587L)
        }
    }

    @ParameterizedTest(name = "delete (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `delete - expenditure or correction blocking deletion`(status: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 3L) } returns
                ProjectPartnerReportStatusAndVersion(reportId = 3L, status, "4.5.8")
        every { expenditurePersistence.existsByProcurementId(547L) } returns true
        every { auditControlCorrectionPersistence.existsByProcurementId(547L) } returns true

        assertThrows<ProcurementIsLinkedToExpenditureOrCorrection> {
            interactor.delete(PARTNER_ID, reportId = 3L, procurementId = 547L)
        }
        verify(exactly = 0) {
            reportProcurementPersistence.deletePartnerReportProcurement(any(), any(), any())
        }
    }

    @ParameterizedTest(name = "delete - not draft (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete - not draft`(status: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 13L) } returns
                ProjectPartnerReportStatusAndVersion(13L, status, "4.5.8")
        every { expenditurePersistence.existsByProcurementId(517L) } returns false
        every { auditControlCorrectionPersistence.existsByProcurementId(517L) } returns false

        assertThrows<ReportAlreadyClosed> { interactor.delete(PARTNER_ID, reportId = 13L, procurementId = 517L) }
        verify(exactly = 0) {
            reportProcurementPersistence.deletePartnerReportProcurement(any(), any(), any())
        }
    }

}
