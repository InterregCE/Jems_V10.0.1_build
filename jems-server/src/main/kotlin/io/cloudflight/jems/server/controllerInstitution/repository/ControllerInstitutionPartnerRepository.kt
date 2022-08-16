package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetailsRow
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ControllerInstitutionPartnerRepository: JpaRepository<ControllerInstitutionPartnerEntity, Long> {


    // outer select is needed because hibernate tries to use the alias of the first table used in the query when it applies the sorting criteria
    @Query(
        """SELECT * FROM (
            SELECT
            cip.institution_id AS institutionId,
            projectPartner.id AS partnerId,
            projectPartner.abbreviation AS partnerName,
            projectPartner.active AS partnerStatus,
            projectPartner.sort_number AS partnerSortNumber,
            projectPartner.role AS partnerRole,
            partnerAddress.nuts_region3 AS partnerNuts3,
            partnerAddress.country AS country,
            partnerAddress.city AS city,
            partnerAddress.street AS street,
            partnerAddress.house_number AS houseNumber,
            project.project_call_id AS callId,
            project.id AS projectId,
            project.custom_identifier AS projectCustomIdentifier,
            project.acronym AS projectAcronym
            FROM optimization_project_version AS opv
            INNER JOIN project FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS project
                ON project.id = opv.project_id
            INNER JOIN project_partner FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS projectPartner
                ON project.id = projectPartner.project_id
            LEFT JOIN project_partner_address FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS partnerAddress
                ON projectPartner.id = partnerAddress.partner_id AND partnerAddress.type = 'Organization'
            LEFT JOIN controller_institution_partner AS cip
                ON cip.partner_id = projectPartner.id
        ) as assignment

        """,
        countQuery = """
            SELECT count(projectPartner.id)
            FROM optimization_project_version AS opv
            INNER JOIN project FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS project
            ON project.id = opv.project_id
            INNER JOIN project_partner FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS projectPartner
            ON project.id = projectPartner.project_id
            LEFT JOIN project_partner_address FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS partnerAddress
            ON projectPartner.id = partnerAddress.partner_id AND partnerAddress.type = 'Organization'
            LEFT JOIN controller_institution_partner AS cip
            ON cip.partner_id = projectPartner.id
        """,
        nativeQuery = true
    )
    fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetailsRow>

    fun findAllByPartnerIdIn(partnerIds: Set<Long>): List<ControllerInstitutionPartnerEntity>

    fun findAllByInstitutionId(institutionId: Long): List<ControllerInstitutionPartnerEntity>

    @Query(
        """
        SELECT new io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers(
            cip.institutionId,
            ciu.id.user.id,
            cip.partnerProjectId
        )
        FROM controller_institution_partner AS cip
        INNER JOIN controller_institution_user AS ciu
            ON cip.institutionId = ciu.id.controllerInstitutionId
        WHERE cip.partnerProjectId IN :partnerProjectIds
        """
    )
    fun getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(partnerProjectIds: Set<Long>): List<InstitutionPartnerAssignmentWithUsers>

    @Query(
        """
        SELECT ciu.accessLevel
        FROM #{#entityName} AS cip
        INNER JOIN controller_institution_user AS ciu
            ON cip.institutionId = ciu.id.controllerInstitutionId
        WHERE cip.partnerId = :partnerId AND ciu.id.user.id = :userId
        """
    )
    fun getControllerUserAccessLevelForPartner(userId: Long, partnerId: Long): UserInstitutionAccessLevel?
}
