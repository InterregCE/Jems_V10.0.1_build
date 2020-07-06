package io.cloudflight.ems.service;

import io.cloudflight.ems.api.dto.user.InputPassword
import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.repository.UserRoleRepository
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
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    companion object {
        private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun findOneByEmail(email: String): UserWithCredentials? {
        return userRepository.findOneByEmail(email)?.let { user ->
            return UserWithCredentials(user.toOutputUser(), user.password)
        }
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputUser {
        return userRepository.findOneById(id)?.toOutputUser()
            ?: throw ResourceNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<OutputUser> {
        return userRepository.findAll(pageable).map { it.toOutputUser() }
    }

    @Transactional
    override fun create(user: InputUserCreate): OutputUser {
        val role = userRoleRepository.findByIdOrNull(user.userRoleId!!)
            ?: throwNotFound("User with id ${user.userRoleId} was not found.")

        val passwordEncoded = passwordEncoder.encode(user.email)
        val createdUser = userRepository.save(user.toEntity(role, passwordEncoded)).toOutputUser()
        auditService.logEvent(Audit.userCreated(securityService.currentUser, createdUser))
        return createdUser
    }

    @Transactional
    override fun registerApplicant(user: InputUserRegistration): OutputUser {
        val role = userRoleRepository.findOneByName(APPLICANT_USER)
            ?: throwNotFound("The default applicant role cannot be found in the system.")

        val passwordEncoded = passwordEncoder.encode(user.password)
        val createdUser = userRepository.save(user.toEntity(role, passwordEncoded)).toOutputUser()
        auditService.logEvent(Audit.applicantRegistered(createdUser))
        return createdUser
    }

    @Transactional
    override fun update(newUser: InputUserUpdate): OutputUser {
        val oldUser = userRepository.findByIdOrNull(newUser.id)
            ?: throwNotFound("User with id ${newUser.id} was not found.")
        val oldData = oldUser.toOutputUser()

        val toUpdate = oldUser.copy(
            email = getNewEmailIfChanged(oldUser, newUser),
            name = newUser.name,
            surname = newUser.surname,
            userRole = getNewRoleIfChanged(oldUser, newUser)
        )

        val updatedUser = userRepository.save(toUpdate).toOutputUser()
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

        userRepository.save(
            account.copy(password = passwordEncoder.encode(passwordData.password))
        )
        auditService.logEvent(Audit.passwordChanged(securityService.currentUser, account.toOutputUser()))
    }

    private fun <T>throwNotFound(msg: String): T {
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

    private fun writeChangeAuditMessages(oldUser: OutputUser, newUser: OutputUser) {
        if (oldUser.userRole.id != newUser.userRole.id)
            auditService.logEvent(
                Audit.userRoleChanged(securityService.currentUser, newUser)
            )

        val changes = getListOfChangedUserData(oldUser, newUser)
        if (changes.isNotEmpty())
            auditService.logEvent(
                Audit.userDataChanged(securityService.currentUser, oldUser.id!!, changes)
            )
    }

    private fun getListOfChangedUserData(oldUser: OutputUser, newUser: OutputUser): Map<String, Pair<String, String>> {
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
