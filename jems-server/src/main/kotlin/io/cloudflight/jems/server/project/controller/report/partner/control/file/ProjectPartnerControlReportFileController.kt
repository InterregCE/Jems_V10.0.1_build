package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerControlReportFileApi
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerControlReportFileController(
    private val generateReportControlCertificate: GenerateReportControlCertificateInteractor
): ProjectPartnerControlReportFileApi  {

    override fun generateControlReportCertificate(partnerId: Long, reportId: Long) {
        generateReportControlCertificate.generateCertificate(partnerId, reportId)
    }
}