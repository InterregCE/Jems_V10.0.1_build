package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportIdentificationTargetGroupRepository :
    JpaRepository<ProjectPartnerReportIdentificationTargetGroupEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportIdentificationTargetGroupEntity.withTranslations")
    fun findAllByReportIdentificationEntityOrderBySortNumber(
        reportIdentificationEntity: ProjectPartnerReportIdentificationEntity,
    ): List<ProjectPartnerReportIdentificationTargetGroupEntity>

}
