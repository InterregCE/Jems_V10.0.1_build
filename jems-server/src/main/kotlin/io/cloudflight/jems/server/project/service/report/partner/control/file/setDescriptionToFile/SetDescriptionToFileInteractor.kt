package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile

interface SetDescriptionToFileInteractor {
    fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String)
}
