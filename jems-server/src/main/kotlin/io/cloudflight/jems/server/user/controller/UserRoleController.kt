package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.UserRoleApi
import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.user.service.UserRoleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRoleController(
    val userRoleService: UserRoleService
) : UserRoleApi {

    @PreAuthorize("hasRole('$ADMINISTRATOR')")
    override fun list(pageable: Pageable): Page<OutputUserRole> {
        return userRoleService.findAll(pageable = pageable)
    }

}
