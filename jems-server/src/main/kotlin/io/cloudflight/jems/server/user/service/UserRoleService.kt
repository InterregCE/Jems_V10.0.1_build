package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.OutputUserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRoleService {

    fun findAll(pageable: Pageable): Page<OutputUserRole>

}
