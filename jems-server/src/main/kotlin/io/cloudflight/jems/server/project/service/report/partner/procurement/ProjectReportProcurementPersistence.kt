package io.cloudflight.jems.server.project.service.report.partner.procurement

import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectReportProcurementPersistence {

    fun getById(partnerId: Long, procurementId: Long): ProjectPartnerReportProcurement

    fun getProcurementsForReportIds(reportIds: Set<Long>, pageable: Pageable): Page<ProjectPartnerReportProcurementSummary>

    fun getProcurementContractNamesForReportIds(reportIds: Set<Long>): Set<Pair<Long, String>>

    fun countProcurementsForPartner(partnerId: Long): Long

    fun updatePartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurement: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement

    fun createPartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurement: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement

    fun deletePartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
    )

}
