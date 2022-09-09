package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentRow
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
            partnerAddress.nuts_region3_code AS partnerNuts3Code,
            partnerAddress.country AS country,
            partnerAddress.country_code AS countryCode,
            partnerAddress.city AS city,
            partnerAddress.postal_code AS postalCode,
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


    // Returns institution-partner assignments to delete if project partners nuts do not match the institution nuts
    @Query(
        """
            SELECT e FROM #{#entityName} e
            INNER JOIN project_partner_address ppa
                ON e.partnerId = ppa.addressId.partnerId AND ppa.addressId.type = 'Organization'
             WHERE (ppa.address.nutsRegion3Code, ppa.address.countryCode) NOT IN
                (SELECT cin.id.nutsRegion3Id, nuts1.country.id FROM controller_institution_nuts AS cin
                  INNER JOIN nuts_region_3 AS nuts3
                             ON cin.id.nutsRegion3Id = nuts3.id
                  INNER JOIN nuts_region_2 nuts2
                             ON nuts3.region2.id = nuts2.id
                  INNER JOIN nuts_region_1 nuts1
                             ON nuts2.region1.id = nuts1.id
                  WHERE e.institutionId = cin.id.institutionId
                )
            AND e.partnerProjectId = :projectId
        """
    )
    fun getInstitutionPartnerAssignmentsToDeleteByProjectId(projectId: Long): List<ControllerInstitutionPartnerEntity>


    /*
        Returns institution-partner assignments to delete if institution nuts do not match the assigned partner nuts.
        Only the last approved version of the partner nuts is compared with the institution nuts.
     */
    @Query(
    """
        SELECT
        cip.institution_id as institutionId,
        cip.partner_id as partnerId,
        cip.partner_project_id as partnerProjectId
        FROM controller_institution_partner AS cip
        INNER JOIN optimization_project_version as opv on opv.project_id = cip.partner_project_id
        INNER JOIN project_partner_address FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS ppa
            ON cip.partner_id = ppa.partner_id AND ppa.type = 'Organization'
        WHERE (ppa.nuts_region3_code, ppa.country_code) NOT IN
                (SELECT cin.nuts_region_3_id, nuts1.nuts_country_id FROM controller_institution_nuts AS cin
                  INNER JOIN nuts_region_3 AS nuts3
                             ON cin.nuts_region_3_id = nuts3.id
                  INNER JOIN nuts_region_2 nuts2
                             ON nuts3.nuts_region_2_id = nuts2.id
                  INNER JOIN nuts_region_1 nuts1
                             ON nuts2.nuts_region_1_id = nuts1.id
                  WHERE cin.controller_institution_id = :institutionId
                )
        AND cip.institution_id = :institutionId
    """,
        nativeQuery = true
    )
    fun getInstitutionPartnerAssignmentsToDeleteByInstitutionId(institutionId: Long): List<InstitutionPartnerAssignmentRow>
}
