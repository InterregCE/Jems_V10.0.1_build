package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.InputPassword
import io.cloudflight.jems.api.user.dto.InputUserCreate
import io.cloudflight.jems.api.user.dto.InputUserRegistration
import io.cloudflight.jems.api.user.dto.InputUserUpdate
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.server.dto.UserWithCredentials
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun findOneByEmail(email: String): UserWithCredentials?

    fun getById(id: Long): OutputUserWithRole

    fun findAll(pageable: Pageable): Page<OutputUserWithRole>

    fun create(user: InputUserCreate): OutputUserWithRole

    fun registerApplicant(user: InputUserRegistration): OutputUserWithRole

    fun update(newUser: InputUserUpdate): OutputUserWithRole

    fun changePassword(userId: Long, passwordData: InputPassword)

}
