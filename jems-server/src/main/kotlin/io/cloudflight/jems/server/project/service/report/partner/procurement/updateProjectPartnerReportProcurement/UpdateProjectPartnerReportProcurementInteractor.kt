package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate

interface UpdateProjectPartnerReportProcurementInteractor {

    fun update(
        partnerId: Long,
        reportId: Long,
        procurementNew: List<ProjectPartnerReportProcurementUpdate>,
    ): List<ProjectPartnerReportProcurement>

}
