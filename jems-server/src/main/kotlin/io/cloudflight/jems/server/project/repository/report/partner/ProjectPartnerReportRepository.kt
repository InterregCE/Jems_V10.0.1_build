package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportBaseData
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.CertificateSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportIdentificationSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.stream.Stream

@Repository
interface ProjectPartnerReportRepository : JpaRepository<ProjectPartnerReportEntity, Long> {

    @Query(
        """
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary(
            report.id,
            project_partner.project.id,
            report.identification.projectIdentifier,
            report.partnerId,
            report.identification.partnerRole,
            report.identification.partnerNumber,
            report.identification.partnerAbbreviation,
            report.number,
            report.status,
            report.applicationFormVersion,
            report.firstSubmission,
            report.lastReSubmission,
            report.controlEnd,
            report.createdAt,
            reportProject.id,
            reportProject.number,
            report_co_fin.sumTotalEligibleAfterControl,
            report_co_fin.sumCurrent,
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
        LEFT JOIN project_partner project_partner
            ON report.partnerId = project_partner.id
        WHERE report.partnerId IN :partnerIds AND report.status IN :statuses
    """
    )
    fun findAllByPartnerIdInAndStatusIn(partnerIds: Set<Long>, statuses: Set<ReportStatus>, pageable: Pageable): Page<ReportSummary>

    fun getAllByPartnerIdInAndStatusIn(partnerIds: Set<Long>, statuses: Set<ReportStatus>): List<ProjectPartnerReportEntity>

    @Query(
        """
        SELECT new io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportBaseData(
            report.id,
            report.partnerId,
            report.applicationFormVersion,
            report.number
        )
        FROM #{#entityName} AS report
        LEFT JOIN project_partner projectPartner
            ON report.partnerId = projectPartner.id
        WHERE projectPartner.project.id = :projectId
        ORDER BY projectPartner.project.id ASC, projectPartner.sortNumber ASC, report.number ASC
    """
    )
    fun findAllPartnerReportsBaseDataByProjectId(projectId: Long): Stream<ProjectPartnerReportBaseData>

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

    fun findAllByProjectReportId(projectReportId: Long): List<ProjectPartnerReportEntity>

    @Query(
        """
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.model.ReportIdentificationSummary(
            report.id,
            report.number,
            report_co_fin.sumTotalEligibleAfterControl,
            report.partnerId,
            report.identification.partnerNumber,
            report.identification.partnerRole,
            report_identification.startDate,
            report_identification.endDate,
            report_identification.periodNumber,
            report_period.startMonth,
            report_period.endMonth,
            report_period.periodBudget,
            report_period.periodBudgetCumulative,
            report_identification.nextReportForecast
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
        WHERE report.projectReport.id = :projectReportId
    """
    )
    fun findAllIdentificationSummariesByProjectReportId(projectReportId: Long): List<ReportIdentificationSummary>

    fun findAllByPartnerIdInAndProjectReportNullAndStatus(partnerIds: Set<Long>, status: ReportStatus): List<ProjectPartnerReportEntity>

    fun existsByPartnerIdAndId(partnerId: Long, id: Long): Boolean

    fun existsByPartnerIdAndStatusIn(partnerId: Long, statuses: Set<ReportStatus>): Boolean

    fun findAllByPartnerIdAndStatusInOrderByNumberDesc(partnerId: Long, statuses: Collection<ReportStatus>): List<ProjectPartnerReportEntity>

    fun findByIdAndPartnerId(id: Long, partnerId: Long): ProjectPartnerReportEntity

    fun findFirstByPartnerIdOrderByIdDesc(partnerId: Long): ProjectPartnerReportEntity?

    fun findFirstByPartnerIdAndStatusOrderByControlEndDesc(partnerId: Long, status: ReportStatus): ProjectPartnerReportEntity?

    fun countAllByPartnerId(partnerId: Long): Int

    @Query("SELECT report.id FROM #{#entityName} report WHERE report.partnerId = :partnerId AND report.id < :reportId")
    fun getReportIdsForPartnerBefore(partnerId: Long, reportId: Long): Set<Long>

    @Query(
        """
        SELECT new kotlin.Pair(
            report.partnerId,
            COALESCE(SUM(report_co_fin.sumTotalEligibleAfterControl), 0)
        )
        FROM #{#entityName} AS report
        LEFT JOIN #{#entityName}_expenditure_co_financing report_co_fin
            ON report.id = report_co_fin.reportEntity.id
        WHERE report.projectReport.id = :projectReportId
        GROUP BY report.partnerId
    """
    )
    fun findTotalAfterControlPerPartner(projectReportId: Long): List<Pair<Long, BigDecimal>>

    fun findAllByProjectReportDeadlineIdIn(deadlineIds: Set<Long>): List<ProjectPartnerReportEntity>

}
