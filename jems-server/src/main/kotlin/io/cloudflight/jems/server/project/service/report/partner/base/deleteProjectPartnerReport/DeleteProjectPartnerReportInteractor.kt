package io.cloudflight.jems.server.project.service.report.partner.base.deleteProjectPartnerReport

interface DeleteProjectPartnerReportInteractor {

    fun delete(partnerId: Long, reportId: Long)
}
