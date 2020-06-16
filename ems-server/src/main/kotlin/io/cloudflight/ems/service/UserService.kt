package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun getByEmail(email: String): OutputUser?

    fun getUsers(pageable: Pageable): Page<OutputUser>

}
