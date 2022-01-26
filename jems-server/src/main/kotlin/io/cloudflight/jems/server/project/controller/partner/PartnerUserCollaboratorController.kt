package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.dto.assignment.PartnerUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdatePartnerUserCollaboratorDTO
import io.cloudflight.jems.api.project.partner.PartnerUserCollaboratorApi
import io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner.AssignUserCollaboratorToPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator.GetPartnerUserCollaboratorsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class PartnerUserCollaboratorController(
    private val assignUserCollaboratorToPartner: AssignUserCollaboratorToPartnerInteractor,
    private val getPartnerUserCollaborators: GetPartnerUserCollaboratorsInteractor
) : PartnerUserCollaboratorApi {

    override fun listAllPartnerCollaborators(projectId: Long): Set<PartnerUserCollaboratorDTO> =
        getPartnerUserCollaborators.getPartnerCollaborators(projectId).toDto()

    override fun updatePartnerUserCollaborators(
        projectId: Long,
        partnerId: Long,
        users: Set<UpdatePartnerUserCollaboratorDTO>
    ): Set<PartnerUserCollaboratorDTO> =
        assignUserCollaboratorToPartner.updateUserAssignmentsOnPartner(projectId, partnerId, users.toModel()).toDto()
}
