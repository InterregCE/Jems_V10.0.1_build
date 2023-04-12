package io.cloudflight.jems.server.project.repository.report.project.resultPrinciple

import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProjectReportProjectResultRepository : JpaRepository<ProjectReportProjectResultEntity, Long> {

    @EntityGraph(value = "ProjectReportProjectResultEntity.withTranslations")
    fun findByProjectReportId(reportId: Long): List<ProjectReportProjectResultEntity>

    @EntityGraph(value = "ProjectReportProjectResultEntity.withTranslations")
    fun findByProjectReportIdAndResultNumber(reportId: Long, resultNumber: Int): ProjectReportProjectResultEntity

    @Query("""
        SELECT new kotlin.Pair(
            result.resultNumber,
            COALESCE(SUM(result.currentReport), 0)
        )
        FROM #{#entityName} result
        WHERE result.projectReport.id IN :reportIds
        GROUP BY result.resultNumber
    """)
    fun getCumulativeValues(reportIds: Set<Long>): List<Pair<Int, BigDecimal>>

    fun deleteByProjectReportId(projectReportId: Long)
}
