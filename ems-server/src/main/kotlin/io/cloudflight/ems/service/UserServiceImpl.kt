package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.user.InputPassword
import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import io.cloudflight.ems.security.ADMINISTRATOR
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
    override fun getById(id: Long): OutputUser {
        return accountRepository.findOneById(id)?.toOutputUser()
            ?: throw ResourceNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUser> {
        return accountRepository.findAll(pageable).map { it.toOutputUser() }
    }

    @Transactional
    override fun create(user: InputUserCreate): OutputUser {
        val role = accountRoleRepository.findByIdOrNull(user.accountRoleId!!)
            ?: throwNotFound("User with id ${user.accountRoleId} was not found.")

        val passwordEncoded = passwordEncoder.encode(user.email)
        val createdUser = accountRepository.save(user.toEntity(role, passwordEncoded)).toOutputUser()
        auditService.logEvent(Audit.userCreated(securityService.currentUser, createdUser))
        return createdUser
    }

    @Transactional
    override fun registerApplicant(user: InputUserRegistration): OutputUser {
        val role = accountRoleRepository.findOneByName(APPLICANT_USER)
            ?: throwNotFound("The default applicant role cannot be found in the system.")

        val passwordEncoded = passwordEncoder.encode(user.password)
        val createdUser = accountRepository.save(user.toEntity(role, passwordEncoded)).toOutputUser()
        auditService.logEvent(Audit.applicantRegistered(createdUser))
        return createdUser
    }

    @Transactional
    override fun update(newUser: InputUserUpdate): OutputUser {
        val oldUser = accountRepository.findByIdOrNull(newUser.id)
            ?: throwNotFound("User with id ${newUser.id} was not found.")
        val oldData = oldUser.toOutputUser()

        val toUpdate = oldUser.copy(
            email = getNewEmailIfChanged(oldUser, newUser),
            name = newUser.name,
            surname = newUser.surname,
            accountRole = getNewRoleIfChanged(oldUser, newUser)
        )

        val updatedUser = accountRepository.save(toUpdate).toOutputUser()
        writeChangeAuditMessages(oldUser = oldData, newUser = updatedUser)
        return updatedUser
    }

    @Transactional
    override fun changePassword(userId: Long, passwordData: InputPassword) {
        val account = accountRepository.findByIdOrNull(userId)
            ?: throwNotFound("User with id $userId was not found.")

        if (!securityService.currentUser!!.isAdmin
            && !passwordEncoder.matches(passwordData.oldPassword, account.password))
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nFieldErrors = mapOf("password" to I18nFieldError("user.password.not.match"))
            )

        accountRepository.save(
            account.copy(password = passwordEncoder.encode(passwordData.password))
        )
        auditService.logEvent(Audit.passwordChanged(securityService.currentUser, account.toOutputUser()))
    }

    private fun <T>throwNotFound(msg: String): T {
        logger.error(msg)
        throw ResourceNotFoundException()
    }

    private fun getNewRoleIfChanged(oldUser: Account, newUser: InputUserUpdate): AccountRole {
        val newRoleId = newUser.accountRoleId

        if (oldUser.accountRole.id == newRoleId)
            return oldUser.accountRole
        else if (!securityService.currentUser!!.hasRole(ADMINISTRATOR))
            throw I18nValidationException(httpStatus = HttpStatus.FORBIDDEN)

        return accountRoleRepository.findByIdOrNull(newRoleId)
            ?: throwNotFound("User role with id $newRoleId was not found.")
    }

    private fun getNewEmailIfChanged(oldUser: Account, newUser: InputUserUpdate): String {
        if (oldUser.email == newUser.email)
            return oldUser.email

        val existing = accountRepository.findOneByEmail(newUser.email)
        if (existing == null || existing.id == oldUser.id)
            return newUser.email

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("email" to I18nFieldError("user.email.not.unique"))
        )
    }

    private fun writeChangeAuditMessages(oldUser: OutputUser, newUser: OutputUser) {
        if (oldUser.userRole.id != newUser.userRole.id)
            auditService.logEvent(
                Audit.userRoleChanged(securityService.currentUser, newUser)
            )

        if (oldUser.email != newUser.email || oldUser.name != newUser.name || oldUser.surname != newUser.surname)
            auditService.logEvent(
                Audit.userDataChanged(securityService.currentUser, oldUser, newUser)
            )
    }

}
