package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate.DownloadReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates.ListReportControlCertificatesInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate.SetDescriptionToCertificateInteractor
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

}