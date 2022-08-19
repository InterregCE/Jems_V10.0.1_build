package io.cloudflight.jems.server.project.service.report.partner.identification.control.updateProjectPartnerControlReportIdentification

import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReportChange

interface UpdateProjectPartnerControlReportIdentificationInteractor {

    fun updateControlIdentification(
        partnerId: Long,
        reportId: Long,
        data: ProjectPartnerControlReportChange,
    ): ProjectPartnerControlReport

}
