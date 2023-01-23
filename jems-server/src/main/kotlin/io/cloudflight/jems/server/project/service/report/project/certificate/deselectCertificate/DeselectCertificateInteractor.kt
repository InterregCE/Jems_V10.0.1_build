package io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate

interface DeselectCertificateInteractor {

    fun deselectCertificate(projectId: Long, reportId: Long, certificateId: Long)

}
