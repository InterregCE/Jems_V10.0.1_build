package io.cloudflight.jems.api.user

import io.cloudflight.jems.api.project.dto.UserPermissionFilterDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.api.user.dto.PasswordDTO
import io.cloudflight.jems.api.user.dto.UserChangeDTO
import io.cloudflight.jems.api.user.dto.UserDTO
import io.cloudflight.jems.api.user.dto.UserSearchRequestDTO
import io.cloudflight.jems.api.user.dto.UserSummaryDTO
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

@Api("User")
@RequestMapping("/api/user")
interface UserApi {

    @ApiOperation("Returns the users")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping("list",consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun list(pageable: Pageable, @RequestBody(required = false) searchRequest: UserSearchRequestDTO?): Page<UserSummaryDTO>

    @ApiOperation("Creates new User")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@RequestBody user: UserChangeDTO): UserDTO

    @ApiOperation("Updates a User")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUser(@RequestBody user: UserChangeDTO): UserDTO

    @ApiOperation("Returns a user by id")
    @GetMapping("/byId/{id}")
    fun getById(@PathVariable id: Long): UserDTO

    @ApiOperation("Changes password of any user (admin only)")
    @PutMapping("/byId/{userId}/password", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resetPassword(@PathVariable userId: Long, @RequestBody newPassword: String)

    @ApiOperation("Changes my password")
    @PutMapping("/changeMyPassword", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun changeMyPassword(@RequestBody passwordData: PasswordDTO)

    @ApiOperation("Returns list of Users, which do have or do NOT have requested permissions")
    @PostMapping("/filterUsersByPermissions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun listUsersByPermissions(@RequestBody userPermissionFilter: UserPermissionFilterDTO): List<UserSummaryDTO>

}
