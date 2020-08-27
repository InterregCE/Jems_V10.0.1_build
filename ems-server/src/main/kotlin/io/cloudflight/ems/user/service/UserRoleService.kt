package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.OutputUserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRoleService {

    fun findAll(pageable: Pageable): Page<OutputUserRole>

}
