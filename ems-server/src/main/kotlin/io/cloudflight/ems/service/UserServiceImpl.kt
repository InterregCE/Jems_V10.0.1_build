package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.repository.AccountRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(private val accountRepository: AccountRepository) : UserService {

    @Transactional(readOnly = true)
    override fun findOneByEmail(email: String): Account? {
        // TODO map the user to something else - LocalCurrentUser maybe
        return accountRepository.findOneByEmail(email);
    }

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): OutputUser? {
        return accountRepository.findOneByEmail(email)?.toOutputUser()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUser> {
        return accountRepository.findAll(pageable).map { it.toOutputUser() }
    }
}
