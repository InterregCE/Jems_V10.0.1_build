package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.assignment.PartnerUserCollaboratorDTO
import io.cloudflight.jems.api.project.dto.assignment.UpdatePartnerUserCollaboratorDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("Project Partner User Collaborator")
@RequestMapping("/api/projectPartnerCollaborators/{projectId}")
interface PartnerUserCollaboratorApi {

    @ApiOperation("Retrieves users that can collaborate on this project partner team")
    @GetMapping
    fun listAllPartnerCollaborators(@PathVariable projectId: Long): Set<PartnerUserCollaboratorDTO>

    @ApiOperation("Assigns a list of Users for monitoring to Partners")
    @PutMapping("/forPartner/{partnerId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePartnerUserCollaborators(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestBody users: Set<UpdatePartnerUserCollaboratorDTO>
    ): Set<PartnerUserCollaboratorDTO>
}
