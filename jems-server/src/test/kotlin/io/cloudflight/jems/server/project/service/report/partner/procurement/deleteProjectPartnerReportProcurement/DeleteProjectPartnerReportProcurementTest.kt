package io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
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
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @InjectMockKs
    lateinit var interactor: DeleteProjectPartnerReportProcurement

    @BeforeEach
    fun setup() {
        clearMocks(reportProcurementPersistence)
    }

    @ParameterizedTest(name = "delete (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun delete(status: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 3L) } returns
            ProjectPartnerReportStatusAndVersion(status, "4.5.8")
        every { reportProcurementPersistence.deletePartnerReportProcurement(PARTNER_ID, reportId = 3L, 587L) } answers { }

        interactor.delete(PARTNER_ID, reportId = 3L, procurementId = 587L)
        verify(exactly = 1) {
            reportProcurementPersistence.deletePartnerReportProcurement(PARTNER_ID, reportId = 3L, procurementId = 587L)
        }
    }

    @ParameterizedTest(name = "delete - not draft (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete - not draft`(status: ReportStatus) {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 3L) } returns
            ProjectPartnerReportStatusAndVersion(status, "4.5.8")

        assertThrows<ReportAlreadyClosed> { interactor.delete(PARTNER_ID, reportId = 3L, procurementId = 587L) }
    }

}
