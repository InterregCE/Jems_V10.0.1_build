package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.assignment.PartnerCollaboratorLevelDTO
import io.cloudflight.jems.api.project.dto.assignment.PartnerUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdatePartnerUserCollaboratorDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner User Collaborator")
interface PartnerUserCollaboratorApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_USER_COLLABORATOR =
            "/api/projectPartnerCollaborators"
    }

    @ApiOperation("Retrieves users that can collaborate on this project partner team")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_USER_COLLABORATOR/forProject/{projectId}")
    fun listAllPartnerCollaborators(@PathVariable projectId: Long): Set<PartnerUserCollaboratorDTO>

    @ApiOperation("Assigns a list of Users for monitoring to Partners")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_USER_COLLABORATOR/forProject/{projectId}/forPartner/{partnerId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePartnerUserCollaborators(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestBody users: Set<UpdatePartnerUserCollaboratorDTO>
    ): Set<PartnerUserCollaboratorDTO>

    @ApiOperation("Check my collaborator-related partner permissions")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_USER_COLLABORATOR/forPartner/{partnerId}/checkMyLevel")
    fun checkMyPartnerLevel(@PathVariable partnerId: Long): PartnerCollaboratorLevelDTO?

    @ApiOperation("Check partner collaborations for current user")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_USER_COLLABORATOR/forProject/{projectId}/myCollaborations")
    fun listCurrentUserPartnerCollaborations(@PathVariable projectId: Long): Set<PartnerUserCollaboratorDTO>

}
