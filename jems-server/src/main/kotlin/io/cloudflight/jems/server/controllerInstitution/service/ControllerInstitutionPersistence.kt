package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.plugin.contract.models.controllerInstitutions.InstitutionPartnerDetailsData
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerSearchRequest
import io.cloudflight.jems.server.controllerInstitution.service.model.ProjectPartnerAssignmentMetadata
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ControllerInstitutionPersistence {

    fun getControllerInstitutions(pageable: Pageable): Page<ControllerInstitutionList>

    fun getControllerInstitutions(partnerIds: Set<Long>): Map<Long, ControllerInstitutionList>

    fun getAllControllerInstitutions(): List<ControllerInstitutionEntity>

    fun getControllerInstitutionById(controllerInstitutionId: Long): ControllerInstitution

    fun getControllerInstitutionsByUserId(userId: Long, pageable: Pageable): Page<ControllerInstitutionList>

    fun getInstitutionUserByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUser>

    fun createControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun updateControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution

    fun updateControllerInstitutionUsers(
        institutionId: Long,
        usersToUpdate: Set<ControllerInstitutionUser>,
        usersIdsToDelete: Set<Long> = emptySet()
    ): Set<ControllerInstitutionUser>

    fun getInstitutionUsersByInstitutionId(institutionId: Long): List<ControllerInstitutionUser>

    fun getControllerUsersForReportByInstitutionId(institutionId: Long): List<UserSimple>

    fun getControllerInstitutionUsersByInstitutionIds(institutionIds: Set<Long>): List<ControllerInstitutionUser>

    fun getInstitutionPartnerAssignments(pageable: Pageable, searchRequest: InstitutionPartnerSearchRequest): Page<InstitutionPartnerDetails>

    fun getAllInstitutionPartnerAssignments(): Sequence<InstitutionPartnerDetailsData>

    fun assignInstitutionToPartner(
        partnerIdsToRemove: Set<Long>,
        assignmentsToSave: List<InstitutionPartnerAssignment>
    ): List<InstitutionPartnerAssignment>

    fun getInstitutionPartnerAssignmentsByInstitutionId(institutionId: Long): List<InstitutionPartnerAssignment>

    fun getRelatedUserIdsForProject(projectId: Long): Set<Long>

    fun getRelatedUserIdsForPartner(partnerId: Long): Set<Long>

    fun getControllerUserAccessLevelForPartner(userId: Long, partnerId: Long): UserInstitutionAccessLevel?

    fun getRelatedProjectAndPartnerIdsForUser(userId: Long): Map<Long, Set<Long>>

    fun getInstitutionPartnerAssignmentsToDeleteByProjectId(projectId: Long): List<InstitutionPartnerAssignment>

    fun getInstitutionPartnerAssignmentsToDeleteByInstitutionId(institutionId: Long): List<InstitutionPartnerAssignment>

    fun updatePartnerDataInAssignments(partners: Collection<ProjectPartnerAssignmentMetadata>)

    fun deletePartnerDataInAssignmentsForProject(projectId: Long)

    fun getNutsAvailableForUser(userId: Long): List<OutputNuts>
}
