package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.getProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.ProjectPartnerReportProcurementGdprAttachmentPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class GetProjectPartnerReportProcurementGdprAttachmentServiceTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 2876L
        private val YEARS_AGO_50 = ZonedDateTime.now().minusYears(50)

        private val attachment1 = ProjectReportProcurementFile(
            id = 270,
            reportId = 528L,
            createdInThisReport = true,
            name = "name 270",
            type = JemsFileType.ProcurementAttachment,
            uploaded = YEARS_AGO_50,
            author = UserSimple(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 6281245L,
            description = "desc 270"
        )
        private val attachment2 = ProjectReportProcurementFile(
            id = 271,
            reportId = 244L,
            createdInThisReport = true,
            name = "name 271",
            type = JemsFileType.ProcurementAttachment,
            uploaded = YEARS_AGO_50,
            author = UserSimple(48L, "dummy@email48", name = "Dummy", surname = "Surname"),
            size = 2968954L,
            description = "desc 271"
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectPartnerReportProcurementPersistence

    @MockK
    lateinit var reportProcurementGdprAttachmentPersistence: ProjectPartnerReportProcurementGdprAttachmentPersistence

    @MockK
    lateinit var sensitiveDataAuthorizationService: SensitiveDataAuthorizationService

    @InjectMockKs
    lateinit var service: GetProjectPartnerReportProcurementGdprAttachmentService

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, reportProcurementPersistence, reportProcurementGdprAttachmentPersistence)
    }

    @Test
    fun `getAttachment - with access to sensitive data`() {
        val reportId = 528L
        val procurementId = 152L

        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns reportId
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns procurementId
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = procurementId) } returns procurement

        every{ sensitiveDataAuthorizationService.canViewPartnerSensitiveData(PARTNER_ID) } returns true
        every { reportProcurementGdprAttachmentPersistence.getGdprAttachmentsBeforeAndIncludingReportId(procurementId, reportId) } returns
            listOf(attachment1, attachment2)


        assertThat(service.getAttachment(PARTNER_ID, reportId = reportId, procurementId = procurementId))
            .containsExactly(
                attachment1.copy(createdInThisReport = true),
                attachment2.copy(createdInThisReport = false),
            )
    }

    @Test
    fun `getAttachment - no access to sensitive data`() {
        val reportId = 528L
        val procurementId = 152L

        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns reportId
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns procurementId
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = procurementId) } returns procurement

        every{ sensitiveDataAuthorizationService.canViewPartnerSensitiveData(PARTNER_ID) } returns false
        every { reportProcurementGdprAttachmentPersistence.getGdprAttachmentsBeforeAndIncludingReportId(procurementId, reportId) } returns
            listOf(attachment1, attachment2)

        assertThat(service.getAttachment(PARTNER_ID, reportId = reportId, procurementId = procurementId))
            .containsExactly(
                attachment1.copy(createdInThisReport = true),
                attachment2.copy(createdInThisReport = false)
            )
    }
}
