package io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate

interface GenerateVerificationCertificateInteractor {

    fun generateCertificate(projectId: Long, reportId: Long, pluginKey: String)
}
