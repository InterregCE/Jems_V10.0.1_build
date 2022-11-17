package io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.UpdateProjectPartnerReportIdentification

interface UpdateProjectPartnerReportIdentificationInteractor {

    fun updateIdentification(
        partnerId: Long,
        reportId: Long,
        data: UpdateProjectPartnerReportIdentification,
    ): ProjectPartnerReportIdentification

}
