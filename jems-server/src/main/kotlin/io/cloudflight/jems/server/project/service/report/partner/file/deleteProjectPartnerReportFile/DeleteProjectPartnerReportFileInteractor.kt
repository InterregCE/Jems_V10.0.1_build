package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

interface DeleteProjectPartnerReportFileInteractor {

    fun delete(partnerId: Long, fileId: Long)

}
