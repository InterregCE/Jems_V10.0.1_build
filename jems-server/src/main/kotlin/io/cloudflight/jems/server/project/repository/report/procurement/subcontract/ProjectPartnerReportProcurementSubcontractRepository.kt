package io.cloudflight.jems.server.project.repository.report.procurement.subcontract

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.subcontract.ProjectPartnerReportProcurementSubcontractEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportProcurementSubcontractRepository :
    JpaRepository<ProjectPartnerReportProcurementSubcontractEntity, Long> {

    fun findTop50ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
        procurementId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportProcurementSubcontractEntity>

    fun findTop50ByProcurementAndCreatedInReportIdOrderByCreatedInReportIdAscIdAsc(
        procurement: ProjectPartnerReportProcurementEntity,
        reportId: Long,
    ): List<ProjectPartnerReportProcurementSubcontractEntity>

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.procurement.id = :procurementId AND e.createdInReportId < :reportId")
    fun countSubcontractorsCreatedBefore(procurementId: Long, reportId: Long): Long

}
