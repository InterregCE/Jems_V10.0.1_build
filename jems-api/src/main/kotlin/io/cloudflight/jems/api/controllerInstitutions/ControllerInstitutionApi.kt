package io.cloudflight.jems.api.controllerInstitutions

import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionListDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerDetailsDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.UpdateControllerInstitutionDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionAssignmentDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerAssignmentDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.UserInstitutionAccessLevelDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Api("Controller Institutions Api")
interface ControllerInstitutionApi {

    companion object {
        private const val ENDPOINT_API_CONTROLLERS = "/api/controller/institution"
    }

    @ApiOperation("Gets all the controllers")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_CONTROLLERS)
    fun getControllers(pageable: Pageable): Page<ControllerInstitutionListDTO>

    @ApiOperation("Returns a controller institution by id")
    @GetMapping("$ENDPOINT_API_CONTROLLERS/{institutionId}")
    fun getControllerInstitutionById(@PathVariable institutionId: Long): ControllerInstitutionDTO

    @ApiOperation("Creates institution controller")
    @PostMapping("$ENDPOINT_API_CONTROLLERS/create", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createController(
        @RequestBody controllerData: UpdateControllerInstitutionDTO,
    ): ControllerInstitutionDTO

    @ApiOperation("Updates institution controller")
    @PutMapping("$ENDPOINT_API_CONTROLLERS/{institutionId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateController(
        @PathVariable institutionId: Long,
        @RequestBody controllerData: UpdateControllerInstitutionDTO,
    ): ControllerInstitutionDTO

    @ApiOperation("Get all institution partner assignments")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_CONTROLLERS/assignments")
    fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetailsDTO>

    @ApiOperation("Assign institutions to partners")
    @PostMapping("$ENDPOINT_API_CONTROLLERS/assign", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun assignInstitutionToPartner(
        @RequestBody institutionPartnerAssignments: ControllerInstitutionAssignmentDTO
    ): List<InstitutionPartnerAssignmentDTO>

    @ApiOperation("Get access level of the controller institution user over the partner")
    @GetMapping("$ENDPOINT_API_CONTROLLERS/assignments/{userId}/{partnerId}")
    fun getControllerUserAccessLevelForPartner(
        @PathVariable userId: Long,
        @PathVariable partnerId: Long,
    ): UserInstitutionAccessLevelDTO?
}
