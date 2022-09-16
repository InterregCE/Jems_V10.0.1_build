package io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile

interface SetDescriptionToProjectPartnerReportFileInteractor {

    fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String)

}
