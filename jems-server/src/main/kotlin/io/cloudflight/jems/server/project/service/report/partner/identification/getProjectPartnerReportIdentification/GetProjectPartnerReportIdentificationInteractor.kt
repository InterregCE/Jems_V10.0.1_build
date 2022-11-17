package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification

interface GetProjectPartnerReportIdentificationInteractor {

    fun getIdentification(partnerId: Long, reportId: Long): ProjectPartnerReportIdentification

}
