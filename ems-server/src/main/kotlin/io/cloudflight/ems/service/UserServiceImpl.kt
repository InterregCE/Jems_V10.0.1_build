package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.service.UserDtoUtilClass.Companion.getDtoFrom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepo: UserRepository
): UserService {

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): OutputUser? {
        return userRepo.findByEmail(email)?.let { getDtoFrom(it) }
    }

    @Transactional(readOnly = true)
    override fun getUsers(pageable: Pageable): Page<OutputUser> {
        return userRepo.findAll(pageable).map { getDtoFrom(it) }
    }

}
