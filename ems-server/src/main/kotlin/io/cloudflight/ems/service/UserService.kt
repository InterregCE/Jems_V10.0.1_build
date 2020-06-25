package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputUser
import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun findOneByEmail(email: String): UserWithCredentials?

    fun getByEmail(email: String): OutputUser?

    fun findAll(pageable: Pageable): Page<OutputUser>

    fun create(user: InputUser): OutputUser

}
