package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportDesignatedControllerEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectPartnerReportDesignatedControllerRepository :
    JpaRepository<ProjectPartnerReportDesignatedControllerEntity, ProjectPartnerReportEntity> {
    fun findByReportEntityIdAndReportEntityPartnerId(
        reportId: Long,
        partnerId: Long,
    ): ProjectPartnerReportDesignatedControllerEntity
}
