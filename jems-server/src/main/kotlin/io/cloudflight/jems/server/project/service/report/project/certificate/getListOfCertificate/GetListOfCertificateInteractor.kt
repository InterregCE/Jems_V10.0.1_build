package io.cloudflight.jems.server.project.service.report.project.certificate.getListOfCertificate

import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetListOfCertificateInteractor {

    fun listCertificates(projectId: Long, reportId: Long, pageable: Pageable): Page<PartnerReportCertificate>

}
