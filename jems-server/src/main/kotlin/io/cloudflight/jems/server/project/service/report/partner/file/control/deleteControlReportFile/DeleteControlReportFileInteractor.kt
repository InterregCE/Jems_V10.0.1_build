package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

interface DeleteControlReportFileInteractor {

    fun delete(partnerId: Long, reportId: Long, fileId: Long)

}
