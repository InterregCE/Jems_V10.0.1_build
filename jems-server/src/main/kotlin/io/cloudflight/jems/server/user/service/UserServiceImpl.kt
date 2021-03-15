package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.InputPassword
import io.cloudflight.jems.api.user.dto.InputUserCreate
import io.cloudflight.jems.api.user.dto.InputUserRegistration
import io.cloudflight.jems.api.user.dto.InputUserUpdate
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.common.validator.PASSWORD_REGEX
import io.cloudflight.jems.server.config.AppSecurityProperties
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.user.model.UserWithCredentials
import io.cloudflight.jems.server.user.repository.UserRepository
import io.cloudflight.jems.server.user.repository.UserRoleRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val auditService: AuditService,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
    private val passwordEncoder: PasswordEncoder,
    private val generalValidatorService: GeneralValidatorService,
    private val appSecurityProperties: AppSecurityProperties
) : UserService {

    companion object {
        private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
        private const val PASSWORD_FIELD_NAME = "password"
        private const val PASSWORD_ERROR_KEY = "user.password.constraints.not.satisfied"
    }

    @Transactional(readOnly = true)
    override fun findOneByEmail(email: String): UserWithCredentials? {
        return userRepository.findOneByEmail(email)?.let { user ->
            return UserWithCredentials(user.toOutputUserWithRole(), user.password)
        }
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputUserWithRole {
        return userRepository.findOneById(id)?.toOutputUserWithRole()
            ?: throw ResourceNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUserWithRole> {
        return userRepository.findAll(pageable).map { it.toOutputUserWithRole() }
    }

    @Transactional
    override fun create(user: InputUserCreate): OutputUserWithRole {
        val role = userRoleRepository.findByIdOrNull(user.userRoleId!!)
            ?: throwNotFound("User with id ${user.userRoleId} was not found.")

        val password = appSecurityProperties.defaultPasswordPrefix.plus(user.email)
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.matches(
                password,
                PASSWORD_REGEX,
                PASSWORD_FIELD_NAME,
                PASSWORD_ERROR_KEY
            )
        )
        val passwordEncoded = passwordEncoder.encode(password)
        val createdUser = userRepository.save(user.toEntity(role, passwordEncoded)).toOutputUserWithRole()
        auditService.logEvent(userCreated(securityService.currentUser, createdUser))
        return createdUser
    }

    @Transactional
    override fun registerApplicant(user: InputUserRegistration): OutputUserWithRole {
        val role = userRoleRepository.findOneByName(APPLICANT_USER)
            ?: throwNotFound("The default applicant role cannot be found in the system.")
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.matches(
                user.password,
                PASSWORD_REGEX,
                PASSWORD_FIELD_NAME,
                PASSWORD_ERROR_KEY
            )
        )
        val passwordEncoded = passwordEncoder.encode(user.password)
        val createdUser = userRepository.save(user.toEntity(role, passwordEncoded)).toOutputUserWithRole()
        auditPublisher.publishEvent(applicantRegistered(this, createdUser))
        return createdUser
    }

    @Transactional
    override fun update(newUser: InputUserUpdate): OutputUserWithRole {
        val oldUser = userRepository.findByIdOrNull(newUser.id)
            ?: throwNotFound("User with id ${newUser.id} was not found.")
        val oldData = oldUser.toOutputUserWithRole()

        val toUpdate = oldUser.copy(
            email = getNewEmailIfChanged(oldUser, newUser),
            name = newUser.name,
            surname = newUser.surname,
            userRole = getNewRoleIfChanged(oldUser, newUser)
        )

        val updatedUser = userRepository.save(toUpdate).toOutputUserWithRole()
        writeChangeAuditMessages(oldUser = oldData, newUser = updatedUser)
        return updatedUser
    }

    @Transactional
    override fun changePassword(userId: Long, passwordData: InputPassword) {
        val account = userRepository.findByIdOrNull(userId)
            ?: throwNotFound("User with id $userId was not found.")

        if (!securityService.currentUser!!.isAdmin
            && !passwordEncoder.matches(passwordData.oldPassword, account.password))
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nFieldErrors = mapOf("password" to I18nFieldError("user.password.not.match"))
            )

        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.matches(
                passwordData.password,
                PASSWORD_REGEX,
                PASSWORD_FIELD_NAME,
                PASSWORD_ERROR_KEY
            )
        )
        userRepository.save(
            account.copy(password = passwordEncoder.encode(passwordData.password))
        )
        auditService.logEvent(passwordChanged(securityService.currentUser, account.toOutputUser()))
    }

    private fun <T> throwNotFound(msg: String): T {
        logger.error(msg)
        throw ResourceNotFoundException()
    }

    private fun getNewRoleIfChanged(oldUser: User, newUser: InputUserUpdate): UserRole {
        val newRoleId = newUser.userRoleId

        if (oldUser.userRole.id == newRoleId)
            return oldUser.userRole
        else if (!securityService.currentUser!!.hasRole(ADMINISTRATOR))
            throw I18nValidationException(httpStatus = HttpStatus.FORBIDDEN)

        return userRoleRepository.findByIdOrNull(newRoleId)
            ?: throwNotFound("User role with id $newRoleId was not found.")
    }

    private fun getNewEmailIfChanged(oldUser: User, newUser: InputUserUpdate): String {
        if (oldUser.email == newUser.email)
            return oldUser.email

        val existing = userRepository.findOneByEmail(newUser.email)
        if (existing == null || existing.id == oldUser.id)
            return newUser.email

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("email" to I18nFieldError("user.email.not.unique"))
        )
    }

    private fun writeChangeAuditMessages(oldUser: OutputUserWithRole, newUser: OutputUserWithRole) {
        if (oldUser.userRole.id != newUser.userRole.id)
            auditService.logEvent(
                userRoleChanged(securityService.currentUser, newUser)
            )

        val changes = getListOfChangedUserData(oldUser, newUser)
        if (changes.isNotEmpty())
            auditService.logEvent(userDataChanged(oldUser.id!!, changes))
    }

    private fun getListOfChangedUserData(oldUser: OutputUserWithRole, newUser: OutputUserWithRole): Map<String, Pair<String, String>> {
        val result = mutableMapOf<String, Pair<String, String>>()
        if (oldUser.email != newUser.email)
            result.put("email", Pair(oldUser.email, newUser.email))
        if (oldUser.name != newUser.name)
            result.put("name", Pair(oldUser.name, newUser.name))
        if (oldUser.surname != newUser.surname)
            result.put("surname", Pair(oldUser.surname, newUser.surname))
        return result
    }

}
