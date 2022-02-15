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

@Api("User Role")
interface UserRoleApi {

    companion object {
        private const val ENDPOINT_API_USER_ROLE = "/api/role"
    }

    @ApiOperation("Returns available user roles")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_USER_ROLE)
    fun list(pageable: Pageable): Page<UserRoleSummaryDTO>

    @ApiOperation("Creates new User Role")
    @PostMapping(ENDPOINT_API_USER_ROLE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUserRole(@RequestBody role: UserRoleCreateDTO): UserRoleDTO

    @ApiOperation("Updates existing User Role")
    @PutMapping(ENDPOINT_API_USER_ROLE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUserRole(@RequestBody role: UserRoleDTO): UserRoleDTO

    @ApiOperation("Returns a user role by id")
    @GetMapping("$ENDPOINT_API_USER_ROLE/byId/{id}")
    fun getById(@PathVariable id: Long): UserRoleDTO

    @ApiOperation("Returns the default user role id")
    @GetMapping("$ENDPOINT_API_USER_ROLE/default")
    fun getDefault(): Long?

    @ApiOperation("Set user role default")
    @PutMapping("$ENDPOINT_API_USER_ROLE/default", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setDefault(@RequestBody userRoleId: Long)
}
