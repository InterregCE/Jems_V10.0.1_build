package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ControllerInstitutionPartnerRepository: JpaRepository<ControllerInstitutionPartnerEntity, Long> {


    @Query(
        """
        SELECT new io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails(
            cip.institutionId,
            pp.id,
            pp.abbreviation,
            pp.active,
            pp.role,
            pp.sortNumber,
            ppa.address.nutsRegion3,
            concat(ppa.address.street, ', ', ppa.address.houseNumber, ', ', ppa.address.city, ', ', ppa.address.country),
            p.call.id,
            p.id,
            p.acronym
        )
        FROM project AS p
        INNER JOIN project_status AS ps
            ON p.currentStatus.id = ps.id
        INNER JOIN project_partner AS pp
            ON p.id = pp.project.id
        LEFT JOIN project_partner_address AS ppa
            ON pp.id = ppa.addressId.partnerId AND ppa.addressId.type = 'Organization'
        LEFT JOIN controller_institution_partner AS cip
            ON cip.partnerId = pp.id
        WHERE ps.status NOT IN :statuses
        """
    )
    fun getInstitutionPartnerAssignments(pageable: Pageable, statuses: List<ApplicationStatus>): Page<InstitutionPartnerDetails>

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
