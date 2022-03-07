package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportRepository : JpaRepository<ProjectPartnerReportEntity, Long> {

    fun findAllByPartnerId(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportEntity>

    fun findByIdAndPartnerId(id: Long, partnerId: Long): ProjectPartnerReportEntity

    @Query("SELECT COALESCE(MAX(report.number), 0) FROM #{#entityName} report WHERE report.partnerId = :partnerId")
    fun getMaxNumberForPartner(partnerId: Long): Int

    fun countAllByPartnerId(partnerId: Long): Int

}
