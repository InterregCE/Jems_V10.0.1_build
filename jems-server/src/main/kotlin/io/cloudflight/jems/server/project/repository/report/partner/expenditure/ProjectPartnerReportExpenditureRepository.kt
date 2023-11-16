package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportExpenditureRepository : JpaRepository<PartnerReportExpenditureCostEntity, Long> {

    fun findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
        reportId: Long,
        partnerId: Long,
    ): MutableList<PartnerReportExpenditureCostEntity>

    fun findAllByPartnerReportIdOrderById(reportId: Long, pageable: Pageable): Page<PartnerReportExpenditureCostEntity>

    fun findAllByIdIn(ids: Set<Long>, pageable: Pageable): Page<PartnerReportExpenditureCostEntity>

    fun findAllByPartnerReportProjectReportId(projectReportId: Long): List<PartnerReportExpenditureCostEntity>

    fun findByPartnerReportIdOrderByIdDesc(reportId: Long): MutableList<PartnerReportExpenditureCostEntity>

    fun existsByPartnerReportPartnerIdAndPartnerReportIdAndId(partnerId: Long, reportId: Long, expenditureId: Long): Boolean

    fun existsByPartnerReportPartnerIdAndAttachmentIdAndGdprTrue(partnerId: Long, fileId: Long): Boolean
}
