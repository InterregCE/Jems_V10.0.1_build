package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationError
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountRoleRepository: AccountRoleRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    companion object {
        private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun findOneByEmail(email: String): UserWithCredentials? {
        return accountRepository.findOneByEmail(email)?.let { user ->
            return UserWithCredentials(user.toOutputUser(), user.password)
        }
    }

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): OutputUser? {
        return accountRepository.findOneByEmail(email)?.toOutputUser()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUser> {
        return accountRepository.findAll(pageable).map { it.toOutputUser() }
    }

    @Transactional
    override fun create(user: InputUserCreate): OutputUser {
        val role = accountRoleRepository.findById(user.accountRoleId!!)
        if (role.isEmpty)
            throw I18nValidationError(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nFieldErrors = mapOf("accountRoleId" to I18nFieldError("user.accountRoleId.does.not.exist"))
            )

        val passwordEncoded = passwordEncoder.encode(user.email);
        val createdUser = accountRepository.save(user.toEntity(role.get(), passwordEncoded)).toOutputUser()
        auditService.logEvent(Audit.userCreated(securityService.currentUser, createdUser))
        return createdUser
    }

    @Transactional
    override fun registerApplicant(user: InputUserRegistration): OutputUser {
        val role = accountRoleRepository.findOneByName(APPLICANT_USER)
        if (role == null) {
            logger.error("The default applicant role cannot be found in the system.")
            throw ResourceNotFoundException()
        }

        val passwordEncoded = passwordEncoder.encode(user.email);
        val createdUser = accountRepository.save(user.toEntity(role, passwordEncoded)).toOutputUser()
        auditService.logEvent(Audit.applicantRegistered(createdUser))
        return createdUser
    }

}
