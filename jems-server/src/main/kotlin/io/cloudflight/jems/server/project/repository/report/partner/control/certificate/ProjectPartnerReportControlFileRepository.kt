package io.cloudflight.jems.server.project.repository.report.partner.control.certificate

import io.cloudflight.jems.server.project.entity.report.control.certificate.PartnerReportControlFileEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportControlFileRepository : JpaRepository<PartnerReportControlFileEntity, Long> {
    fun findAllByReportId(reportId: Long, page: Pageable): Page<PartnerReportControlFileEntity>

    fun findByReportIdAndId(reportId: Long, id: Long): PartnerReportControlFileEntity
}
