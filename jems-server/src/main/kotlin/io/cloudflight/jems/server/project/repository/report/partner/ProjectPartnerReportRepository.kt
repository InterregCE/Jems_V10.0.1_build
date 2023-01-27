package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.CertificateSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportRepository : JpaRepository<ProjectPartnerReportEntity, Long> {

    @Query(
        """
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary(
            report.id,
            report.number,
            report.status,
            report.applicationFormVersion,
            report.firstSubmission,
            report.controlEnd,
            report.createdAt,
            reportProject.id,
            reportProject.number,
            report_co_fin.sumTotalEligibleAfterControl,
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
        LEFT JOIN #{#entityName}_expenditure_co_financing report_co_fin
            ON report.id = report_co_fin.reportEntity.id
        LEFT JOIN report_project reportProject
            ON report.projectReport.id = reportProject.id
        WHERE report.partnerId = :partnerId
    """
    )
    fun findAllByPartnerId(partnerId: Long, pageable: Pageable): Page<ReportSummary>

    @Query("""
         SELECT
            report.id AS partnerReportId,
            report.number AS partnerReportNumber,
            report.partner_id AS partnerId,
            report.partner_role AS partnerRole,
            report.partner_number AS partnerNumber,
            report_co_fin.sum_total_eligible_after_control AS totalEligibleAfterControl,
            report.control_end AS controlEnd,
            report_project.id AS projectReportId,
            report_project.number AS projectReportNumber
        FROM #{#entityName} AS report
        LEFT JOIN #{#entityName}_expenditure_co_financing report_co_fin
            ON report.id = report_co_fin.report_id
        LEFT JOIN report_project
            ON report.project_report_id = report_project.id
        WHERE report.partner_id IN :partnerIds AND report.status = 'Certified'
    """, countQuery = """
        SELECT COUNT(*)
        FROM #{#entityName} AS report
        WHERE report.partner_id IN :partnerIds AND report.status = 'Certified'
    """, nativeQuery = true)
    fun findAllCertificates(partnerIds: Set<Long>, pageable: Pageable): Page<CertificateSummary>

    fun findAllByPartnerIdInAndProjectReportNullAndStatus(partnerIds: Set<Long>, status: ReportStatus): List<ProjectPartnerReportEntity>

    fun existsByPartnerIdAndId(partnerId: Long, id: Long): Boolean

    @Query("SELECT e.id FROM #{#entityName} e WHERE e.partnerId = :partnerId AND e.status IN :statuses")
    fun findAllIdsByPartnerIdAndStatusIn(partnerId: Long, statuses: Collection<ReportStatus>): Set<Long>

    fun findByIdAndPartnerId(id: Long, partnerId: Long): ProjectPartnerReportEntity

    fun findFirstByPartnerIdOrderByIdDesc(partnerId: Long): ProjectPartnerReportEntity?

    fun findFirstByPartnerIdAndStatusOrderByIdDesc(partnerId: Long, status: ReportStatus): ProjectPartnerReportEntity?

    fun countAllByPartnerId(partnerId: Long): Int

    @Query("SELECT report.id FROM #{#entityName} report WHERE report.partnerId = :partnerId AND report.id < :reportId")
    fun getReportIdsForPartnerBefore(partnerId: Long, reportId: Long): Set<Long>

}
