package io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement

import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange

interface CreateProjectPartnerReportProcurementInteractor {

    fun create(
        partnerId: Long,
        reportId: Long,
        procurementData: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement

}
