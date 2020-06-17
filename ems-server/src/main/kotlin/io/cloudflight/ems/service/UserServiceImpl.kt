package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    @Transactional(readOnly = true)
    override fun findOneByEmail(email: String): User? {
        // TODO map the user to something else - LocalCurrentUser maybe
        return userRepository.findOneByEmail(email);
    }

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): OutputUser? {
        return userRepository.findOneByEmail(email)?.toOutputUser()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUser> {
        return userRepository.findAll(pageable).map { it.toOutputUser() }
    }
}
