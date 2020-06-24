package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.InputAccount
import io.cloudflight.ems.api.dto.OutputAccount
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationError
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import io.cloudflight.ems.security.service.SecurityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountRoleRepository: AccountRoleRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : AccountService {

    @Transactional(readOnly = true)
    override fun findOneByEmail(email: String): UserWithCredentials? {
        return accountRepository.findOneByEmail(email)?.let { user ->
            return UserWithCredentials(user.toOutputUser(), user.password)
        }
    }

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): OutputAccount? {
        return accountRepository.findOneByEmail(email)?.toOutputUser()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputAccount> {
        return accountRepository.findAll(pageable).map { it.toOutputUser() }
    }

    @Transactional
    override fun create(account: InputAccount): OutputAccount {
        val fieldErrors = mutableMapOf<String, I18nFieldError>()
        accountRepository.findOneByEmail(account.email)?.let {
            fieldErrors.put("email", I18nFieldError("user.email.not.unique"))
        }

        val role = accountRoleRepository.findById(account.accountRoleId!!)
        if (role.isEmpty) {
            fieldErrors.put("accountRoleId", I18nFieldError("user.accountRoleId.does.not.exist"))
        }

        if (fieldErrors.isNotEmpty()) {
            throw I18nValidationError(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nFieldErrors = fieldErrors)
        }

        val createdUser = accountRepository.save(account.toEntity(role.get())).toOutputUser()
        auditService.logEvent(Audit.userCreated(securityService.currentUser, createdUser))
        return createdUser
    }

}
