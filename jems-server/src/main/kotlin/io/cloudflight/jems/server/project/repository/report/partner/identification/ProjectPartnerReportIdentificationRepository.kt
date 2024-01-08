package io.cloudflight.jems.server.project.repository.report.partner.identification

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportIdentificationEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectPartnerReportIdentificationRepository :
    JpaRepository<ProjectPartnerReportIdentificationEntity, ProjectPartnerReportEntity> {

    @EntityGraph(value = "ProjectPartnerReportIdentificationEntity.withTranslations")
    fun findByReportEntityIdAndReportEntityPartnerId(
        reportId: Long,
        partnerId: Long,
    ): Optional<ProjectPartnerReportIdentificationEntity>

    @Query("SELECT period_number FROM #{#entityName} WHERE report_id= :reportId", nativeQuery = true)
    fun getPartnerReportPeriod(reportId: Long): Int?

}
