package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.report.repositoryModel.ReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportRepository : JpaRepository<ProjectPartnerReportEntity, Long> {

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.repositoryModel.ReportSummary(
            report.id,
            report.number,
            report.status,
            report.applicationFormVersion,
            report.firstSubmission,
            report.createdAt,
            report_identification.periodNumber,
            report_identification.startDate,
            report_identification.endDate,
            report_period.startMonth,
            report_period.endMonth,
            report_period.periodBudget,
            report_period.periodBudgetCumulative
        )
        FROM #{#entityName} AS report
        LEFT JOIN #{#entityName}_identification AS report_identification
            ON report.id = report_identification.reportId
        LEFT JOIN #{#entityName}_budget_per_period report_period
            ON report.id = report_period.id.report.id AND report_identification.periodNumber = report_period.id.periodNumber
        WHERE report.partnerId = :partnerId
    """)
    fun findAllByPartnerId(partnerId: Long, pageable: Pageable): Page<ReportSummary>

    fun existsByPartnerIdAndId(partnerId: Long, id: Long): Boolean

    @Query("SELECT e.id FROM #{#entityName} e WHERE e.partnerId = :partnerId AND e.status IN :statuses")
    fun findAllIdsByPartnerIdAndStatusIn(partnerId: Long, statuses: Collection<ReportStatus>): Set<Long>

    fun findByIdAndPartnerId(id: Long, partnerId: Long): ProjectPartnerReportEntity

    @Query("SELECT COALESCE(MAX(report.number), 0) FROM #{#entityName} report WHERE report.partnerId = :partnerId")
    fun getMaxNumberForPartner(partnerId: Long): Int

    fun countAllByPartnerId(partnerId: Long): Int

    @Query("SELECT report.id FROM #{#entityName} report WHERE report.partnerId = :partnerId AND report.id < :reportId")
    fun getReportIdsForPartnerBefore(partnerId: Long, reportId: Long): Set<Long>

}
