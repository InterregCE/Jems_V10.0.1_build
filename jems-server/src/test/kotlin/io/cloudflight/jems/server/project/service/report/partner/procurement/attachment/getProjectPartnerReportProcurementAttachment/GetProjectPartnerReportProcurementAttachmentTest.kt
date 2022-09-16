package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.ProjectReportProcurementAttachmentPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class GetProjectPartnerReportProcurementAttachmentTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 2876L
        private val YEARS_AGO_50 = ZonedDateTime.now().minusYears(50)

        private val attachment1 = ProjectReportProcurementFile(
            id = 270,
            reportId = 528L,
            createdInThisReport = true,
            name = "name 270",
            type = ProjectPartnerReportFileType.ProcurementAttachment,
            uploaded = YEARS_AGO_50,
            author = UserSimple(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 6281245L,
            description = "desc 270",
        )
        private val attachment2 = ProjectReportProcurementFile(
            id = 271,
            reportId = 244L,
            createdInThisReport = true,
            name = "name 271",
            type = ProjectPartnerReportFileType.ProcurementAttachment,
            uploaded = YEARS_AGO_50,
            author = UserSimple(48L, "dummy@email48", name = "Dummy", surname = "Surname"),
            size = 2968954L,
            description = "desc 271",
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var reportProcurementAttachmentPersistence: ProjectReportProcurementAttachmentPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurementAttachment

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, reportProcurementPersistence, reportProcurementAttachmentPersistence)
    }

    @Test
    fun getAttachment() {
        val reportId = 528L
        val procurementId = 152L

        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns reportId
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns procurementId
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = procurementId) } returns procurement

        every { reportProcurementAttachmentPersistence.getAttachmentsBeforeAndIncludingReportId(procurementId, reportId) } returns
            listOf(attachment1, attachment2)

        assertThat(interactor.getAttachment(PARTNER_ID, reportId = reportId, procurementId = procurementId))
            .containsExactly(
                attachment1.copy(createdInThisReport = true),
                attachment2.copy(createdInThisReport = false),
            )
    }

}
