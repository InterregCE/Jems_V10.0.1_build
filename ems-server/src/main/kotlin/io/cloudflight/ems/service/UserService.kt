package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun findOneByEmail(email: String): UserWithCredentials?

    fun getByEmail(email: String): OutputUser?

    fun getById(id: Long): OutputUser?

    fun findAll(pageable: Pageable): Page<OutputUser>

    fun create(user: InputUserCreate): OutputUser

    fun registerApplicant(user: InputUserRegistration): OutputUser

    fun update(user: InputUserUpdate): OutputUser

}
