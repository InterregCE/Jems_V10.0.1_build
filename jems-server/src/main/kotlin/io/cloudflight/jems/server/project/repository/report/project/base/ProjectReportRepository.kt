package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import java.util.stream.Stream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportRepository : JpaRepository<ProjectReportEntity, Long> {

    @Query("""
        SELECT new io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData(
            report.id,
            report.applicationFormVersion,
            report.number
        )
        FROM #{#entityName} AS report
        WHERE report.projectId = :projectId
        ORDER BY report.number ASC
    """)
    fun findAllProjectReportsBaseDataByProjectId(projectId: Long): Stream<ProjectReportBaseData>

    fun getByIdAndProjectId(id: Long, projectId: Long): ProjectReportEntity

    fun deleteByProjectIdAndId(projectId: Long, id: Long)

    fun findFirstByProjectIdOrderByIdDesc(projectId: Long): ProjectReportEntity?

    fun countAllByProjectId(projectId: Long): Int

    fun findAllByProjectIdAndStatusInOrderByNumberDesc(projectId: Long, statuses: Set<ProjectReportStatus>): List<ProjectReportEntity>

    fun findAllByProjectIdAndDeadlineNotNull(projectId: Long): List<ProjectReportEntity>

    fun findAllByProjectIdAndDeadlineId(projectId: Long, deadlineId: Long): List<ProjectReportEntity>

    fun findAllByProjectIdAndNumberGreaterThan(projectId: Long, number: Number): List<ProjectReportEntity>

    fun existsByProjectIdAndId(projectId: Long, id: Long): Boolean

    fun findByProjectIdAndStatusInAndTypeInOrderByIdDesc(
        projectId: Long,
        statuses: Set<ProjectReportStatus>,
        types: Set<ContractingDeadlineType>,
    ): List<ProjectReportEntity>

    fun findByProjectIdAndStatusInAndDeadlineTypeInOrderByIdDesc(
        projectId: Long,
        statuses: Set<ProjectReportStatus>,
        types: Set<ContractingDeadlineType>,
    ): List<ProjectReportEntity>

}
