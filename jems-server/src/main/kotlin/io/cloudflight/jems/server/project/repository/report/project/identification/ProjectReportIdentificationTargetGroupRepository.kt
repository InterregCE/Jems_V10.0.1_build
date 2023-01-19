package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportIdentificationTargetGroupRepository :
    JpaRepository<ProjectReportIdentificationTargetGroupEntity, Long> {

    @EntityGraph(value = "ProjectReportIdentificationTargetGroupEntity.withTranslations")
    fun findAllByProjectReportEntityOrderBySortNumber(reportIdentificationEntity: ProjectReportEntity):
        List<ProjectReportIdentificationTargetGroupEntity>
}
