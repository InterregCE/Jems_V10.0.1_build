package io.cloudflight.jems.server.project.service.report.partner.identification.control.getProjectPartnerControlReportIdentification

import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReport

interface GetProjectPartnerControlReportIdentificationInteractor {

    fun getControlIdentification(partnerId: Long, reportId: Long): ProjectPartnerControlReport

}
