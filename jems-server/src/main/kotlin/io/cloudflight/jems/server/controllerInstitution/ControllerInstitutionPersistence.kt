package io.cloudflight.jems.server.controllerInstitution

import io.cloudflight.jems.server.controllerInstitution.service.model.*
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional


interface ControllerInstitutionPersistence {

    fun getControllerInstitutions(pageable: Pageable): Page<ControllerInstitutionList>

    fun getControllerInstitutionById(controllerInstitutionId: Long): ControllerInstitution

    fun getControllerInstitutionsByUserId(userId: Long, pageable: Pageable): Page<ControllerInstitutionList>

    fun getInstitutionUserByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUser>

    fun createControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun updateControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun updateControllerInstitutionUsers(
        institutionId: Long,
        userSummaries: List<UserSummary>,
        usersToUpdate: Set<ControllerInstitutionUser>,
        usersIdsToDelete: Set<Long> = emptySet()
    )

    fun getInstitutionUsersByInstitutionId(institutionId: Long): List<ControllerInstitutionUser>

    fun getControllerInstitutionUsersByInstitutionIds(institutionIds: Set<Long>): List<ControllerInstitutionUser>

    fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetails>

    fun assignInstitutionToPartner(
        assignmentsToRemove: List<InstitutionPartnerAssignment>,
        assignmentsToSave: List<InstitutionPartnerAssignment>
    ): List<InstitutionPartnerAssignment>

    fun getInstitutionPartnerAssignmentsByPartnerIdsIn(partnerIds: Set<Long>): List<InstitutionPartnerAssignment>

    fun getInstitutionPartnerAssignmentsByInstitutionId(institutionId: Long): List<InstitutionPartnerAssignment>

    fun getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(partnerProjectIds: Set<Long>): List<InstitutionPartnerAssignmentWithUsers>
}
