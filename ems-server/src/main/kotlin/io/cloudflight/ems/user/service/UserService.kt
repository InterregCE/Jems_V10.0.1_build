package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.InputPassword
import io.cloudflight.ems.api.user.dto.InputUserCreate
import io.cloudflight.ems.api.user.dto.InputUserRegistration
import io.cloudflight.ems.api.user.dto.InputUserUpdate
import io.cloudflight.ems.api.user.dto.OutputUserWithRole
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
