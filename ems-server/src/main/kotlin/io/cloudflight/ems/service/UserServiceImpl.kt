package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
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
import org.springframework.data.repository.findByIdOrNull
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
    override fun getById(id: Long): OutputUser? {
        return accountRepository.findByIdOrNull(id)?.toOutputUser()
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

    @Transactional
    override fun update(user: InputUserUpdate): OutputUser {
        val existingUser = accountRepository.findByIdOrNull(user.id)
            ?: let {
                logger.error("User with id ${user.id} was not found.")
                throw ResourceNotFoundException()
            }

        val newRole = getRole(existingUser, user.accountRoleId)
        writeAuditMessages(existingUser.accountRole, newRole, user.email)

        val updatedUser = existingUser.copy(accountRole = newRole)
        return accountRepository
            .save(updatedUser)
            .toOutputUser()
    }

    private fun getRole(existingUser: Account, newRoleId: Long): AccountRole {
        if (existingUser.accountRole.id == newRoleId) {
            return existingUser.accountRole
        }
        return accountRoleRepository.findByIdOrNull(newRoleId)
            ?: let {
                logger.error("User role with id $newRoleId was not found.")
                throw ResourceNotFoundException()
            }
    }

    private fun writeAuditMessages(existingRole: AccountRole, newRole: AccountRole, userEmail: String) {
        if (existingRole.id == newRole.id) {
            return
        }

        auditService.logEvent(
            Audit.userRoleChanged(
                currentUser = securityService.currentUser,
                newRole = newRole.name,
                userEmail = userEmail
            )
        )
    }

    @Transactional
    override fun changePassword(userId: Long, password: String) {
        val account = accountRepository.findById(userId)
            .orElseThrow { throw ResourceNotFoundException() }

        accountRepository.save(
            account.copy(password = passwordEncoder.encode(password))
        )
        auditService.logEvent(Audit.passwordChanged(securityService.currentUser, account.toOutputUser()))
    }
}
