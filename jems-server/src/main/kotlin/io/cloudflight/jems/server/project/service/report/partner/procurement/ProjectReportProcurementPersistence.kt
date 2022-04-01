package io.cloudflight.jems.server.project.service.report.partner.procurement

import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate

interface ProjectReportProcurementPersistence {

    fun getProcurementIdsForReport(partnerId: Long, reportId: Long): Set<Long>

    fun existsByProcurementId(partnerId: Long, reportId: Long, procurementId: Long): Boolean

    fun getProcurementsForReportIds(reportIds: Set<Long>): List<ProjectPartnerReportProcurement>

    fun getProcurementContractIdsForReportIds(reportIds: Set<Long>): Set<String>

    fun countProcurementsForReportIds(reportIds: Set<Long>): Long

    fun updatePartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurementNew: List<ProjectPartnerReportProcurementUpdate>,
    )

}
