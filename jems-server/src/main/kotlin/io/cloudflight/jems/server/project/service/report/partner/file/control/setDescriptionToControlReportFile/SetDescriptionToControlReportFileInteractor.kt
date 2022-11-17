package io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile

interface SetDescriptionToControlReportFileInteractor {

    fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String)

}
