package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange

interface UpdateProjectPartnerReportProcurementInteractor {

    fun update(
        partnerId: Long,
        reportId: Long,
        procurementData: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement

}
