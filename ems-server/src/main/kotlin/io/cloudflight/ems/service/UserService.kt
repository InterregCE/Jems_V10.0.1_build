package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.InputPassword
import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.dto.UserWithCredentials
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
