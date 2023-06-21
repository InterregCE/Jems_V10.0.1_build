package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.control.file.deleteFileAttachment.DeleteReportControlFileAttachment
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFile.DownloadReportControlFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFileAttachment.DownloadReportControlFileAttachment
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.uploadAttachmentToFile.UploadAttachmentToFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.GenerateReportControlExportInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.listFiles.ListReportControlFilesInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile.SetDescriptionToFileInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test

open class ProjectPartnerControlReportFileControllerTest : UnitTest() {

    @MockK
    lateinit var generateReportCertificate: GenerateReportControlCertificateInteractor

    @MockK
    lateinit var generateReportControlExport: GenerateReportControlExportInteractor

    @MockK
    lateinit var setCertificateFileDescription: SetDescriptionToFileInteractor

    @MockK
    lateinit var listReportControlCertificates: ListReportControlFilesInteractor

    @MockK
    lateinit var downloadReportControlCertificate: DownloadReportControlFileInteractor

    @MockK
    lateinit var deleteReportControlFileAttachment: DeleteReportControlFileAttachment

    @MockK
    lateinit var downloadReportControlFileAttachment: DownloadReportControlFileAttachment

    @MockK
    lateinit var uploadFileToCertificate: UploadAttachmentToFileInteractor

    @InjectMockKs
    lateinit var controller: ProjectPartnerControlReportFileController


    @Test
    fun generateControlCertificate() {
        every {
            generateReportCertificate.generateCertificate(8L, 26L, "plugin-key")
        } answers { }
        controller.generateControlReportCertificate(8L, 26L, "plugin-key")
        verify(exactly = 1) { controller.generateControlReportCertificate(8L, 26L, "plugin-key") }
    }

    @Test
    fun generateControlExport() {
        every {
            generateReportControlExport.export(8L, 26L, "plugin-key")
        } answers { }
        controller.generateControlReportExport(8L, 26L, "plugin-key")
        verify(exactly = 1) { controller.generateControlReportExport(8L, 26L, "plugin-key") }
    }


    @Test
    fun updateControlReportCertificateFileDescription() {
        every { setCertificateFileDescription.setDescription(8L, 26L, 19L, "new description") } answers { }
        controller.updateControlReportFileDescription(8L, 26L, 19L, "new description")
        verify(exactly = 1) {
            controller.updateControlReportFileDescription(
                8L,
                26L,
                19L,
                "new description"
            )
        }
    }

    @Test
    fun deleteControlReportCertificateFiles() {
        every { deleteReportControlFileAttachment
            .deleteReportControlCertificateAttachment(8L, 26L, 1L, 2L) } answers { }
        controller.deleteControlReportAttachment(8L, 26L, 1L, 2L)
        verify(exactly = 1) {
            controller.deleteControlReportAttachment(
                8L,
                26L,
                1L,
                2L
            )
        }
    }


}

