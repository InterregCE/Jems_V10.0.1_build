package io.cloudflight.jems.server.project.repository.partneruser

import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerCollaboratorEntity
import io.cloudflight.jems.server.project.entity.partneruser.UserPartnerId
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserPartnerCollaboratorRepository : JpaRepository<UserPartnerCollaboratorEntity, UserPartnerId> {

    fun findAllByProjectId(projectId: Long): Iterable<UserPartnerCollaboratorEntity>

    fun findAllByIdUserId(userId: Long): Iterable<UserPartnerCollaboratorEntity>

    @Query("""
        SELECT new io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator(
            upc.id.userId,
            upc.id.partnerId,
            a.email,
            upc.level)
        FROM #{#entityName} AS upc
        LEFT JOIN project_partner pp on pp.id = upc.id.partnerId
        LEFT JOIN account a on a.id = upc.id.userId
        WHERE pp.project.id = :projectId
        ORDER BY a.email
    """)
    fun findByProjectId(projectId: Long): Set<PartnerCollaborator>

    @Query("""
        SELECT new io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator(
            upc.id.userId,
            upc.id.partnerId,
            a.email,
            upc.level)
        FROM #{#entityName} AS upc
        LEFT JOIN project_partner pp on pp.id = upc.id.partnerId
        LEFT JOIN account a on a.id = upc.id.userId
        WHERE pp.project.id = :projectId AND a.id = :userId
        ORDER BY a.email
    """)
    fun findAllByIdUserIdAndProjectId(userId: Long, projectId: Long): Set<PartnerCollaborator>

    @Query("""
        SELECT new io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator(
            upc.id.userId,
            upc.id.partnerId,
            a.email,
            upc.level)
        FROM #{#entityName} AS upc
        LEFT JOIN project_partner pp on pp.id = upc.id.partnerId
        LEFT JOIN account a on a.id = upc.id.userId
        WHERE pp.project.id = :projectId AND upc.id.partnerId IN :partnerIds
        ORDER BY a.email
    """)
    fun findAllByProjectAndPartners(projectId: Long, partnerIds: Set<Long>): Set<PartnerCollaborator>

    @Query("""
        SELECT new io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator(
            upc.id.userId,
            upc.id.partnerId,
            a.email,
            upc.level)
        FROM #{#entityName} AS upc
        LEFT JOIN account a on a.id = upc.id.userId
        WHERE upc.id.partnerId = :partnerId
        ORDER BY a.email
    """)
    fun findByPartnerId(partnerId: Long): Set<PartnerCollaborator>

    fun deleteAllByIdIn(id: Collection<UserPartnerId>)

    fun deleteAllByProjectId(projectId: Long)
}
