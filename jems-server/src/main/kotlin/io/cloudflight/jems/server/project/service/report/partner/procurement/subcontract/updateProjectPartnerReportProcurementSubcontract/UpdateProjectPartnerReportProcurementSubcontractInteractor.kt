package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.updateProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.project.service.report.model.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange

interface UpdateProjectPartnerReportProcurementSubcontractInteractor {

    fun update(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        subcontracts: List<ProjectPartnerReportProcurementSubcontractChange>,
    ): List<ProjectPartnerReportProcurementSubcontract>

}
