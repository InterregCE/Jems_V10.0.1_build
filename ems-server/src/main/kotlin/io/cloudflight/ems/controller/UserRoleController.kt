package io.cloudflight.ems.controller

import io.cloudflight.ems.api.UserRoleApi
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.service.UserRoleService
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
