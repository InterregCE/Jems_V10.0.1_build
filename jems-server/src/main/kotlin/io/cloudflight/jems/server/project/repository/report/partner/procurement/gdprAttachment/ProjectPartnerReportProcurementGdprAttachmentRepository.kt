package io.cloudflight.jems.server.project.repository.report.partner.procurement.gdprAttachment

import io.cloudflight.jems.server.project.entity.report.partner.procurement.file.ProjectPartnerReportProcurementGdprFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportProcurementGdprAttachmentRepository :
    JpaRepository<ProjectPartnerReportProcurementGdprFileEntity, Long> {

    fun findTop30ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
        procurementId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportProcurementGdprFileEntity>

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.procurement.id = :procurementId AND e.createdInReportId <= :reportId")
    fun countAttachmentsCreatedBeforeIncludingThis(procurementId: Long, reportId: Long): Long

}
