package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.repository.UserRoleRepository
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
