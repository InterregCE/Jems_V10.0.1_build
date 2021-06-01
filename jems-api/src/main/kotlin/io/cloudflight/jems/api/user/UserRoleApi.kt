package io.cloudflight.jems.api.user

import io.cloudflight.jems.api.user.dto.UserRoleCreateDTO
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.api.user.dto.UserRoleSummaryDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("User Role")
@RequestMapping("/api/role")
interface UserRoleApi {

    @ApiOperation("Returns available user roles")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun list(pageable: Pageable): Page<UserRoleSummaryDTO>

    @ApiOperation("Creates new User Role")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUserRole(@RequestBody role: UserRoleCreateDTO): UserRoleDTO

    @ApiOperation("Updates existing User Role")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUserRole(@RequestBody role: UserRoleDTO): UserRoleDTO

    @ApiOperation("Returns a user role by id")
    @GetMapping("/byId/{id}")
    fun getById(@PathVariable id: Long): UserRoleDTO

}
