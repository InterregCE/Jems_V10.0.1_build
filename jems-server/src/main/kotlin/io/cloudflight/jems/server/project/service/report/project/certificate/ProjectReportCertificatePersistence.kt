package io.cloudflight.jems.server.project.service.report.project.certificate

interface ProjectReportCertificatePersistence {

    fun deselectCertificate(projectReportId: Long, certificateId: Long)

    fun selectCertificate(projectReportId: Long, certificateId: Long)

}
