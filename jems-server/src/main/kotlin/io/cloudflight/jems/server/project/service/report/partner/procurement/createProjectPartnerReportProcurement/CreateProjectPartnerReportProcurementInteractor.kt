package io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement

import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange

interface CreateProjectPartnerReportProcurementInteractor {

    fun create(
        partnerId: Long,
        reportId: Long,
        procurementData: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement

}
