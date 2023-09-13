package io.cloudflight.jems.server.project.service.report.project.verification.certificate.updateDescription

interface UpdateDescriptionProjectReportVerificationCertificateInteractor {

    fun updateDescription(projectId: Long, reportId: Long, fileId: Long, description: String)
}
