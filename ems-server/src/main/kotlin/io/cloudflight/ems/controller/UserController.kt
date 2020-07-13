package io.cloudflight.ems.controller

import io.cloudflight.ems.api.UserApi
import io.cloudflight.ems.api.dto.user.InputPassword
import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    val userService: UserService
) : UserApi {

    @PreAuthorize("hasRole('$ADMINISTRATOR')")
    override fun list(pageable: Pageable): Page<OutputUser> {
        return userService.findAll(pageable = pageable)
    }

    @PreAuthorize("hasRole('$ADMINISTRATOR')")
    override fun createUser(user: InputUserCreate): OutputUserWithRole {
        return userService.create(user)
    }

    @PreAuthorize("@userAuthorization.canUpdateUser(#id)")
    override fun getById(id: Long): OutputUserWithRole {
        return userService.getById(id)
    }

    @PreAuthorize("@userAuthorization.canUpdateUser(#user)")
    override fun update(user: InputUserUpdate): OutputUserWithRole {
        return userService.update(user)
    }

    @PreAuthorize("@userAuthorization.canUpdateUser(#userId)")
    override fun changePassword(userId: Long, passwordData: InputPassword) {
        this.userService.changePassword(userId, passwordData)
    }
}
