package io.cloudflight.jems.server.project.repository.report.file

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.server.project.entity.report.file.QReportProjectFileEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportFileRepository : JpaRepository<ReportProjectFileEntity, Long>, QuerydslPredicateExecutor<ReportProjectFileEntity> {

    fun existsByProjectIdAndId(projectId: Long, fileId: Long): Boolean

    fun existsByPathAndName(path: String, name: String): Boolean

    @Query(
        """
        SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
        FROM #{#entityName} e
        WHERE e.partnerId = :partnerId AND e.path LIKE :pathPrefix% AND e.id = :id
    """
    )
    fun existsByPartnerIdAndPathPrefixAndId(partnerId: Long, pathPrefix: String, id: Long): Boolean

    fun existsByTypeAndId(type: ProjectPartnerReportFileType, id: Long): Boolean

    fun existsByProjectIdAndIdAndTypeIn(
        projectId: Long,
        fileId: Long,
        fileTypes: Set<ProjectPartnerReportFileType>
    ): Boolean

    fun existsByPartnerIdAndIdAndTypeIn(partnerId: Long, fileId: Long, fileTypes: Set<ProjectPartnerReportFileType>): Boolean

    @Query(
        """
        FROM #{#entityName} e
        WHERE e.partnerId = :partnerId AND e.path LIKE :pathPrefix% AND e.id = :id
    """
    )
    fun findByPartnerIdAndPathPrefixAndId(partnerId: Long, pathPrefix: String, id: Long): ReportProjectFileEntity?

    fun findByPartnerIdAndId(partnerId: Long, fileId: Long): ReportProjectFileEntity?

    fun findByTypeAndId(type: ProjectPartnerReportFileType, fileId: Long): ReportProjectFileEntity?

    fun findByProjectIdAndId(projectId: Long, fileId: Long): ReportProjectFileEntity?


    @EntityGraph(value = "ReportProjectFileEntity.user")
    override fun findAll(predicate: Predicate, pageable: Pageable): Page<ReportProjectFileEntity>

}

fun ProjectReportFileRepository.filterAttachment(
    pageable: Pageable,
    indexPrefix: String,
    filterSubtypes: Set<ProjectPartnerReportFileType>,
    filterUserIds: Set<Long>,
): Page<ReportProjectFileEntity> {
    val spec = QReportProjectFileEntity.reportProjectFileEntity
    val expressions = mutableListOf<BooleanExpression>()

    if (indexPrefix.isNotEmpty())
        expressions.add(spec.path.like("$indexPrefix%"))

    if (filterSubtypes.isNotEmpty())
        expressions.add(spec.type.`in`(filterSubtypes))

    if (filterUserIds.isNotEmpty())
        expressions.add(spec.user.id.`in`(filterUserIds))

    return if (expressions.isNotEmpty())
        findAll(expressions.reduce { f, s -> f.and(s) }, pageable)
    else
        findAll(pageable)
}
