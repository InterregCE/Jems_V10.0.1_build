package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.dto.assignment.PartnerCollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.PartnerUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdatePartnerUserCollaboratorDTO
import io.cloudflight.jems.api.project.partner.PartnerUserCollaboratorApi
import io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner.AssignUserCollaboratorToPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator.GetPartnerUserCollaboratorsInteractor
import io.cloudflight.jems.server.project.service.partnerUser.getMyPartnerCollaboratorLevel.GetMyPartnerCollaboratorLevelInteractor
import io.cloudflight.jems.server.project.service.partnerUser.getUserPartnerCollaborations.GetUserPartnerCollaborationsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class PartnerUserCollaboratorController(
    private val assignUserCollaboratorToPartner: AssignUserCollaboratorToPartnerInteractor,
    private val getPartnerUserCollaborators: GetPartnerUserCollaboratorsInteractor,
    private val checkMyPartnerCollaboratorLevel: GetMyPartnerCollaboratorLevelInteractor,
    private val getUserPartnerCollaborations: GetUserPartnerCollaborationsInteractor
) : PartnerUserCollaboratorApi {

    override fun listAllPartnerCollaborators(projectId: Long): Set<PartnerUserCollaboratorDTO> =
        getPartnerUserCollaborators.getPartnerCollaborators(projectId).toDto()

    override fun updatePartnerUserCollaborators(
        projectId: Long,
        partnerId: Long,
        users: Set<UpdatePartnerUserCollaboratorDTO>
    ): Set<PartnerUserCollaboratorDTO> =
        assignUserCollaboratorToPartner.updateUserAssignmentsOnPartner(projectId, partnerId, users.toModel()).toDto()

    override fun checkMyPartnerLevel(partnerId: Long): PartnerCollaboratorLevelDTO? =
        checkMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(partnerId = partnerId)?.toDto()

    override fun listCurrentUserPartnerCollaborations(projectId: Long): Set<PartnerUserCollaboratorDTO> =
        getUserPartnerCollaborations.getUserPartnerCollaborations(projectId).toDto()

}
