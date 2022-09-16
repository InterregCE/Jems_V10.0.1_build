package io.cloudflight.jems.server.project.controller.report.procurement.attachment

import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.file.UserSimpleDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment.ProjectReportProcurementFileDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment.GetProjectPartnerReportProcurementAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.uploadFileToProjectPartnerReportProcurementAttachment.UploadFileToProjectPartnerReportProcurementAttachmentInteractor
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.file
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class ProjectPartnerReportProcurementAttachmentControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 825L
        private val YEARS_AGO_10 = ZonedDateTime.now().minusYears(10)

        private fun dummyAttachment(reportId: Long) = ProjectReportProcurementFile(
            id = 270,
            reportId = reportId,
            createdInThisReport = true,
            name = "name 270",
            type = ProjectPartnerReportFileType.ProcurementAttachment,
            uploaded = YEARS_AGO_10,
            author = UserSimple(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653245L,
            description = "desc 270",
        )

        private fun expectedAttachment(reportId: Long) = ProjectReportProcurementFileDTO(
            id = 270,
            reportId = reportId,
            createdInThisReport = true,
            name = "name 270",
            type = ProjectPartnerReportFileTypeDTO.ProcurementAttachment,
            uploaded = YEARS_AGO_10,
            author = UserSimpleDTO(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653245L,
            sizeString = "637.9 kB",
            description = "desc 270",
        )

        private val fileMetadata = ProjectReportFileMetadata(
            id = 904L,
            name = FILE_NAME,
            uploaded = YEARS_AGO_10,
        )

        private val expectedMetadata = ProjectReportFileMetadataDTO(
            id = 904L,
            name = FILE_NAME,
            uploaded = YEARS_AGO_10,
        )

    }

    @MockK
    lateinit var getAttachment: GetProjectPartnerReportProcurementAttachmentInteractor
    @MockK
    lateinit var uploadFile: UploadFileToProjectPartnerReportProcurementAttachmentInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportProcurementAttachmentController

    @Test
    fun getAttachments() {
        every { getAttachment.getAttachment(PARTNER_ID, reportId = 17L, procurementId = 70L) } returns
            listOf(dummyAttachment(reportId = 17L))
        assertThat(controller.getAttachments(partnerId = PARTNER_ID, reportId = 17L, procurementId = 70L))
            .containsExactly(expectedAttachment(reportId = 17L))
    }

    @Test
    fun uploadAttachment() {
        val projectFileSlot = slot<ProjectFile>()
        every {
            uploadFile.uploadToProcurement(PARTNER_ID, 95L, 555L, capture(projectFileSlot))
        } returns fileMetadata

        assertThat(controller.uploadAttachment(PARTNER_ID, reportId = 95L, procurementId = 555L, file))
            .isEqualTo(expectedMetadata)
        assertThat(projectFileSlot.captured.name).isEqualTo(FILE_NAME)
        assertThat(projectFileSlot.captured.size).isEqualTo(100)
    }

}
