package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract

import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange

interface ProjectReportProcurementSubcontractPersistence {

    fun getSubcontractBeforeAndIncludingReportId(procurementId: Long, reportId: Long): List<ProjectPartnerReportProcurementSubcontract>

    fun updateSubcontract(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        data: List<ProjectPartnerReportProcurementSubcontractChange>,
    ): List<ProjectPartnerReportProcurementSubcontract>

    fun countSubcontractorsCreatedBefore(procurementId: Long, reportId: Long): Long

}
