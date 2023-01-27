package io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate

interface SelectCertificateInteractor {

    fun selectCertificate(projectId: Long, reportId: Long, certificateId: Long)

}
