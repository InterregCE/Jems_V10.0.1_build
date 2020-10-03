package io.cloudflight.jems.server.user.service;

import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.server.user.repository.UserRoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRoleServiceImpl(
    private val userRoleRepository: UserRoleRepository
) : UserRoleService {

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUserRole> {
        return userRoleRepository.findAll(pageable).map { it.toOutputUserRole() }
    }

}
