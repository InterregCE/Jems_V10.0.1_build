package io.cloudflight.jems.server.project.repository.report.procurement.beneficial

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.beneficial.ProjectPartnerReportProcurementBeneficialEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportProcurementBeneficialRepository :
    JpaRepository<ProjectPartnerReportProcurementBeneficialEntity, Long> {

    fun findTop10ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
        procurementId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportProcurementBeneficialEntity>

    fun findTop10ByProcurementAndCreatedInReportIdOrderByCreatedInReportIdAscIdAsc(
        procurement: ProjectPartnerReportProcurementEntity,
        reportId: Long,
    ): List<ProjectPartnerReportProcurementBeneficialEntity>

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.procurement.id = :procurementId AND e.createdInReportId < :reportId")
    fun countOwnersCreatedBefore(procurementId: Long, reportId: Long): Long

}
