package io.cloudflight.jems.server.project.controller.report.project.certificate

import io.cloudflight.jems.api.project.report.project.ProjectReportCertificateApi
import io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate.DeselectCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.certificate.getListOfCertificate.GetListOfCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate.SelectCertificateInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportCertificateController(
    private val getListOfCertificate: GetListOfCertificateInteractor,
    private val deselectCertificate: DeselectCertificateInteractor,
    private val selectCertificate: SelectCertificateInteractor,
) : ProjectReportCertificateApi {

    override fun getProjectReportListOfCertificate(projectId: Long, reportId: Long, pageable: Pageable) =
        getListOfCertificate.listCertificates(projectId, reportId = reportId, pageable = pageable)
            .map { it.toDto(reportId) }

    override fun deselectCertificate(projectId: Long, reportId: Long, certificateId: Long) =
        deselectCertificate.deselectCertificate(projectId, reportId = reportId, certificateId = certificateId)

    override fun selectCertificate(projectId: Long, reportId: Long, certificateId: Long) =
        selectCertificate.selectCertificate(projectId, reportId = reportId, certificateId = certificateId)

}
