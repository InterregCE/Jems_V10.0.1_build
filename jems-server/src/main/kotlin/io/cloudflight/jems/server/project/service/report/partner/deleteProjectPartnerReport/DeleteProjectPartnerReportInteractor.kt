package io.cloudflight.jems.server.project.service.report.partner.deleteProjectPartnerReport

interface DeleteProjectPartnerReportInteractor {

    fun delete(partnerId: Long, reportId: Long)
}
