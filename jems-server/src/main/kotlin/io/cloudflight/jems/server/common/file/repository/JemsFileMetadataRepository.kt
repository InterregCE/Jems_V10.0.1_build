package io.cloudflight.jems.server.common.file.repository

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.entity.QJemsFileMetadataEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface JemsFileMetadataRepository : JpaRepository<JemsFileMetadataEntity, Long>,
    QuerydslPredicateExecutor<JemsFileMetadataEntity> {

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

    fun existsByTypeAndId(type: JemsFileType, id: Long): Boolean

    @Query(
        """
        SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
        FROM #{#entityName} e
        WHERE e.projectId = :projectId AND e.path LIKE :pathPrefix% AND e.id = :id
    """
    )
    fun existsByProjectIdAndPathPrefixAndId(projectId: Long, pathPrefix: String, id: Long): Boolean

    fun existsByProjectIdAndIdAndTypeIn(
        projectId: Long,
        fileId: Long,
        fileTypes: Set<JemsFileType>
    ): Boolean

    fun existsByPartnerIdAndIdAndTypeIn(partnerId: Long, fileId: Long, fileTypes: Set<JemsFileType>): Boolean

    @Query(
        """
        FROM #{#entityName} e
        WHERE e.partnerId = :partnerId AND e.path LIKE :pathPrefix% AND e.id = :id
    """
    )
    fun findByPartnerIdAndPathPrefixAndId(partnerId: Long, pathPrefix: String, id: Long): JemsFileMetadataEntity?

    fun findByPartnerIdAndId(partnerId: Long, fileId: Long): JemsFileMetadataEntity?

    fun findByTypeAndId(type: JemsFileType, fileId: Long): JemsFileMetadataEntity?

    fun findByProjectIdAndId(projectId: Long, fileId: Long): JemsFileMetadataEntity?


    @EntityGraph(value = "FileMetadataEntity.user")
    override fun findAll(predicate: Predicate, pageable: Pageable): Page<JemsFileMetadataEntity>

}

fun JemsFileMetadataRepository.filterAttachment(
    pageable: Pageable,
    indexPrefix: String,
    filterSubtypes: Set<JemsFileType>,
    filterUserIds: Set<Long>,
): Page<JemsFileMetadataEntity> {
    val spec = QJemsFileMetadataEntity.jemsFileMetadataEntity
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
