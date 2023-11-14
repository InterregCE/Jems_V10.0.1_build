package io.cloudflight.jems.server.project.service.report.partner.procurement

import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerReportProcurementPersistence {

    fun getById(partnerId: Long, procurementId: Long): ProjectPartnerReportProcurement

    fun getProcurementsForReportIds(reportIds: Set<Long>, pageable: Pageable): Page<ProjectPartnerReportProcurement>

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

    fun deletePartnerReportProcurement(partnerId: Long, reportId: Long, procurementId: Long)

    fun existsByProcurementIdAndPartnerReportIdIn(procurementId: Long, partnerReportIds: Set<Long>): Boolean
}
