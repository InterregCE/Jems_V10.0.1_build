package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract

interface GetProjectPartnerReportProcurementSubcontractInteractor {

    fun getSubcontract(partnerId: Long, reportId: Long, procurementId: Long): List<ProjectPartnerReportProcurementSubcontract>

}
