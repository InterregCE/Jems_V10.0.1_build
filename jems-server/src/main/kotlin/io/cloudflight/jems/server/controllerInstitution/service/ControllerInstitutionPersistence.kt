package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional


interface ControllerInstitutionPersistence {

    fun getControllerInstitutions(pageable: Pageable): Page<ControllerInstitutionList>

    fun getAllControllerInstitutions(): List<ControllerInstitutionEntity>

    fun getControllerInstitutionById(controllerInstitutionId: Long): ControllerInstitution

    fun getControllerInstitutionsByUserId(userId: Long, pageable: Pageable): Page<ControllerInstitutionList>

    fun getInstitutionUserByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUser>

    fun createControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun updateControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun getAllControllerInstitutionUsersIds(): Set<Long>

    fun updateControllerInstitutionUsers(
        institutionId: Long,
        usersToUpdate: Set<ControllerInstitutionUser>,
        usersIdsToDelete: Set<Long> = emptySet()
    ): Set<ControllerInstitutionUser>

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

    fun getControllerUserAccessLevelForPartner(userId: Long, partnerId: Long): UserInstitutionAccessLevel?

    fun getInstitutionPartnerAssignmentsToDeleteByProjectId(projectId: Long): List<InstitutionPartnerAssignment>
    fun getInstitutionPartnerAssignmentsToDeleteByInstitutionId(institutionId: Long): List<InstitutionPartnerAssignment>
}
