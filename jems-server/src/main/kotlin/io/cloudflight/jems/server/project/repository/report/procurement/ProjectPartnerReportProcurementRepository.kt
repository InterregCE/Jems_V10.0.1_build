package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportProcurementRepository :
    JpaRepository<ProjectPartnerReportProcurementEntity, Long> {

    fun findByReportEntityIdIn(reportIds: Set<Long>, pageable: Pageable): Page<ProjectPartnerReportProcurementEntity>

    fun findByReportEntityPartnerIdAndId(partnerId: Long, id: Long): ProjectPartnerReportProcurementEntity

    fun findByReportEntityPartnerIdAndReportEntityIdAndId(partnerId: Long, reportId: Long, id: Long): ProjectPartnerReportProcurementEntity

    fun findTop50ByReportEntityIdIn(reportIds: Set<Long>): List<ProjectPartnerReportProcurementEntity>

    fun countByReportEntityPartnerId(partnerId: Long): Long

    fun deleteByReportEntityPartnerIdAndReportEntityIdAndId(partnerId: Long, reportId: Long, id: Long)

}
