package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate

interface SetDescriptionToCertificateInteractor {
    fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String)
}