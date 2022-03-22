package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportProcurementRepository :
    JpaRepository<ProjectPartnerReportProcurementEntity, Long> {

    fun findTop50ByReportEntityIdInOrderByReportEntityIdDescIdDesc(
        reportIds: Set<Long>,
    ): List<ProjectPartnerReportProcurementEntity>

    @Query("SELECT e.id FROM #{#entityName} e WHERE e.reportEntity.partnerId=:partnerId AND e.reportEntity.id = :reportId")
    fun findProcurementIdsForReport(partnerId: Long, reportId: Long): Set<Long>

    @Query("SELECT e.contractId FROM #{#entityName} e WHERE e.reportEntity.id IN :reportIds")
    fun findProcurementContractIdsForReportsIn(reportIds: Set<Long>): Set<String>

    fun countByReportEntityIdIn(reportIds: Set<Long>): Long

}
