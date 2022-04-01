package io.cloudflight.jems.server.project.service.report.partner.procurement.uploadFileToProjectPartnerReportProcurement

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private const val PARTNER_ID = 3500L
private const val REPORT_ID = 360L
private const val USER_ID = 698L

internal class UploadFileToProjectPartnerReportProcurementTest : UnitTest() {

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReportProcurement

    @BeforeEach
    fun setup() {
        clearMocks(reportFilePersistence)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }


    @Test
    fun uploadToProcurement() {
        every { reportProcurementPersistence.existsByProcurementId(PARTNER_ID, REPORT_ID, 22L) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns 800L

        val slotFile = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.updatePartnerReportProcurementAttachment(22L, capture(slotFile)) } returns mockResult

        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)

        assertThat(interactor.uploadToProcurement(PARTNER_ID, REPORT_ID, 22L, file)).isEqualTo(mockResult)

        with(slotFile.captured) {
            assertThat(projectId).isEqualTo(800L)
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(name).isEqualTo("file_name.xlsx")
            assertThat(path).isEqualTo("Project/000800/Report/Partner/003500/PartnerReport/000360/Procurement/000022/")
            assertThat(type).isEqualTo(ProjectPartnerReportFileType.Procurement)
            assertThat(size).isEqualTo(50L)
            assertThat(userId).isEqualTo(USER_ID)
        }
    }

    @Test
    fun `uploadToProcurement - not exists`() {
        every { reportProcurementPersistence.existsByProcurementId(PARTNER_ID, REPORT_ID, 22L) } returns false
        assertThrows<ProcurementNotFoundException> { interactor.uploadToProcurement(PARTNER_ID, REPORT_ID, 22L, mockk()) }
    }

}
