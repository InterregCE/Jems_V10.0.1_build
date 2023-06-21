package io.cloudflight.jems.server.project.repository.report.partner.control.overview

import io.cloudflight.jems.server.project.entity.report.control.overview.PartnerReportControlOverviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportControlOverviewRepository: JpaRepository<PartnerReportControlOverviewEntity, Long> {

    fun findByPartnerReportPartnerIdAndPartnerReportId(partnerId: Long, reportId: Long): PartnerReportControlOverviewEntity
}
