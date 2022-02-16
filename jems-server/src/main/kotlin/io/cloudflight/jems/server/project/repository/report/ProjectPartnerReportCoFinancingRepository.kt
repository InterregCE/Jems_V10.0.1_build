package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportCoFinancingRepository :
    JpaRepository<ProjectPartnerReportCoFinancingEntity, ProjectPartnerReportCoFinancingIdEntity> {

    fun findAllByIdReportIdOrderByIdFundSortNumber(reportId: Long): List<ProjectPartnerReportCoFinancingEntity>

}
