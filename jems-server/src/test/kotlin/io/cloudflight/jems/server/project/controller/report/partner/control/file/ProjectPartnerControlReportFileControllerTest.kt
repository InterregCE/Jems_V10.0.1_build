package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.control.file.deleteReportControlCertificateAttachment.DeleteReportControlCertificateAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate.DownloadReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificateAttachment.DownloadReportControlCertificateAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates.ListReportControlCertificatesInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate.SetDescriptionToCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.uploadFileToCertificate.UploadFileToCertificateInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test

open class ProjectPartnerControlReportFileControllerTest: UnitTest() {

    @MockK
    lateinit var  generateReportControlCertificate: GenerateReportControlCertificateInteractor

    @MockK
    lateinit var  setCertificateFileDescription: SetDescriptionToCertificateInteractor

    @MockK
    lateinit var listReportControlCertificates: ListReportControlCertificatesInteractor

    @MockK
    lateinit var downloadReportControlCertificate: DownloadReportControlCertificateInteractor

    @MockK
    lateinit var deleteReportControlCertificateAttachment: DeleteReportControlCertificateAttachmentInteractor

    @MockK
    lateinit var downloadReportControlCertificateAttachment: DownloadReportControlCertificateAttachmentInteractor

    @MockK
    lateinit var uploadFileToCertificate: UploadFileToCertificateInteractor

    @InjectMockKs
    lateinit var controller: ProjectPartnerControlReportFileController


    @Test
    fun generateControlReportCertificate() {
        every { generateReportControlCertificate.generateCertificate(8L, 26L) } answers { }
        controller.generateControlReportCertificate(8L,26L)
        verify(exactly = 1) { controller.generateControlReportCertificate(8L, 26L) }
    }


    @Test
    fun updateControlReportCertificateFileDescription() {
        every { setCertificateFileDescription.setDescription(8L, 26L, 19L, "new description") } answers { }
        controller.updateControlReportCertificateFileDescription(8L, 26L, 19L, "new description")
        verify(exactly = 1) {
            controller.updateControlReportCertificateFileDescription(
                8L,
                26L,
                19L,
                "new description"
            )
        }
    }

    @Test
    fun deleteControlReportCertificateFiles() {
        every { deleteReportControlCertificateAttachment
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
