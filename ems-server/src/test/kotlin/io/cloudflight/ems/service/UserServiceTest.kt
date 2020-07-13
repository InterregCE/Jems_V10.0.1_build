package io.cloudflight.ems.service

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.cloudflight.ems.api.dto.user.InputPassword
import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.repository.UserRoleRepository
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.PROGRAMME_USER
import io.cloudflight.ems.security.config.PasswordConfig
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

class UserServiceTest {

    private val UNPAGED = Pageable.unpaged()

    private val user = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    // data for update
    val oldRole = UserRole(id = 8, name = "role_program")
    val oldUser = User(
        id = 15,
        email = "old@mail.eu",
        name = "OldName",
        surname = "OldSurname",
        userRole = oldRole,
        password = "{bcrypt}\$2a\$10\$uUqunxh6bS/vDkUE8Jsjte02BmwsohtEfLG5xFbAGYwzwfdVW5y7S" // hash for 'old_pass'
    )

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var userRoleRepository: UserRoleRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    private val passwordEncoder: PasswordEncoder = DelegatingPasswordEncoder(
        PasswordConfig.PASSWORD_ENCODER,
        mapOf(PasswordConfig.PASSWORD_ENCODER to BCryptPasswordEncoder())
    )

    @MockK
    lateinit var securityService: SecurityService

    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        userService =
            UserServiceImpl(userRepository, userRoleRepository, auditService, securityService, passwordEncoder)
    }

    @Test
    fun getAllUsers() {
        val userToReturn = User(
            id = 85,
            email = "admin@ems.io",
            name = "Name",
            surname = "Surname",
            userRole = UserRole(9, "admin"),
            password = "hash_pass"
        )
        every { userRepository.findAll(UNPAGED) } returns PageImpl(listOf(userToReturn))

        // test start
        val result = userService.findAll(UNPAGED)

        // assertions:
        assertThat(result.totalElements).isEqualTo(1);

        val expectedUsers = listOf(
            OutputUser(
                id = 85,
                email = "admin@ems.io",
                name = "Name",
                surname = "Surname"
            )
        )
        assertThat(result.stream()).isEqualTo(expectedUsers);
    }

    @Test
    fun getUser_empty() {
        every { userRepository.findOneByEmail(eq("not_existing@ems.io")) } returns null

        val result = userService.findOneByEmail("not_existing@ems.io")
        assertThat(result).isNull();
    }

    @Test
    fun getUserById() {
        val userToReturn = User(
            id = 44,
            email = "admin@ems.io",
            name = "Name",
            surname = "Surname",
            userRole = UserRole(22, "admin"),
            password = "hash_pass"
        )
        every { userRepository.findOneById(eq(44)) } returns userToReturn

        val result = userService.getById(44)
        assertThat(result).isEqualTo(OutputUserWithRole(
            id = 44,
            email = "admin@ems.io",
            name = "Name",
            surname = "Surname",
            userRole = OutputUserRole(22, "admin")
        ))
    }

    @Test
    fun getUserById_notExisting() {
        every { userRepository.findOneById(eq(-1)) } returns null

        assertThrows<ResourceNotFoundException> { userService.getById(-1) }
    }

    @Test
    fun createUser_wrong() {
        every { userRoleRepository.findById(any()) } returns Optional.empty()

        val account = InputUserCreate(
            email = "existing@user.com",
            name = "Ondrej",
            surname = "Tester",
            userRoleId = 10 // does not exist
        )

        assertThrows<ResourceNotFoundException> { userService.create(account) }
    }

    @Test
    fun createUser_OK() {
        every { userRepository.findOneByEmail(eq("new@user.com")) } returns null
        every { userRoleRepository.findById(eq(54)) } returns Optional.of(UserRole(54, "admin_role"))
        every { userRepository.save(any<User>()) } returnsArgument (0)

        val account = InputUserCreate(
            email = "new@user.com",
            name = "Ondrej",
            surname = "Tester",
            userRoleId = 54
        )

        val result = userService.create(account)
        assertEquals("new@user.com", result.email)
        assertEquals("Ondrej", result.name)
        assertEquals("Tester", result.surname)
        assertEquals(OutputUserRole(54, "admin_role"), result.userRole)

        val event = slot<Audit>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertEquals(1, captured.user?.id)
            assertEquals("admin@admin.dev", captured.user?.email)
            assertEquals(AuditAction.USER_CREATED, captured.action)
            assertEquals("new user new@user.com with role admin_role has been created by admin@admin.dev", captured.description)
        }

    }

    @Test
    fun createUser_missingRole() {
        every { userRoleRepository.findByIdOrNull(-1) } returns null

        val user = InputUserCreate(
            email = "new@user.com",
            name = "Ondrej",
            surname = "Tester",
            userRoleId = -1
        )
        assertThrows<ResourceNotFoundException> { userService.create(user) }
    }

    @Test
    fun registerUser_missingApplicantRole() {
        every { userRoleRepository.findOneByName(eq(APPLICANT_USER)) } returns null

        val user = InputUserRegistration(
            email = "new@user.com",
            name = "Ondrej",
            surname = "Tester",
            password = "raw_password"
        )

        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java) as Logger
        logger.addAppender(listAppender)

        assertThrows<ResourceNotFoundException> { userService.registerApplicant(user) }
        Assertions.assertLinesMatch(
            listOf("The default applicant role cannot be found in the system."),
            listAppender.list.map { it.formattedMessage }
        )
    }

    @Test
    fun registerUser_OK() {
        every { userRepository.findOneByEmail(eq("new@user.com")) } returns null
        val role = UserRole(3, "applicant user")
        every { userRoleRepository.findOneByName(eq("applicant user")) } returns role
        every { userRepository.save(any<User>()) } returns User(18, "new@user.com", "Ondrej", "Tester", role, "hash_pass")

        val user = InputUserRegistration(
            email = "new@user.com",
            name = "Ondrej",
            surname = "Tester",
            password = "raw_password"
        )

        val result = userService.registerApplicant(user)
        assertEquals("new@user.com", result.email)
        assertEquals("Ondrej", result.name)
        assertEquals("Tester", result.surname)
        assertEquals(OutputUserRole(3, "applicant user"), result.userRole)

        val event = slot<Audit>()
        verify { auditService.logEvent(capture(event)) }
        assertEquals(18, event.captured.user?.id)
        assertEquals("new@user.com", event.captured.user?.email)
        assertEquals(AuditAction.USER_REGISTERED, event.captured.action)
        assertEquals("new user 'Ondrej Tester' with role 'applicant user' registered", event.captured.description)

    }

    @Test
    fun updateNotExistingUser() {
        every { userRepository.findByIdOrNull(eq<Long>(-1)) } returns null

        val newUser = InputUserUpdate(
            id = -1,
            email = "",
            name = "",
            surname = "",
            userRoleId = 1
        )
        assertThrows<ResourceNotFoundException> { userService.update(newUser) }
    }

    @Test
    fun update_noRoleChange() {
        every { userRepository.save(any<User>()) } returnsArgument (0)

        val newUser = InputUserUpdate(
            id = oldUser.id!!,
            email = "new@email.eu",
            name = "NewName",
            surname = "NewSurname",
            userRoleId = oldRole.id!!
        )
        every { userRepository.findByIdOrNull(eq(oldUser.id!!)) } returns oldUser
        every { userRepository.findOneByEmail(eq(newUser.email)) } returns null

        val result = userService.update(newUser)
        assertThat(result.email).isEqualTo("new@email.eu")
        assertThat(result.name).isEqualTo("NewName")
        assertThat(result.surname).isEqualTo("NewSurname")
        assertThat(result.userRole).isEqualTo(OutputUserRole(id = oldRole.id, name = oldRole.name))
    }

    @ParameterizedTest
    @ValueSource(strings = [ADMINISTRATOR, PROGRAMME_USER, APPLICANT_USER])
    fun update_roleChange(role: String) {
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$role")))
        every { userRepository.save(any<User>()) } returnsArgument (0)

        val newRole = UserRole(id = 9, name = "role_applicant")
        every { userRoleRepository.findById(eq(newRole.id!!)) } returns Optional.of(newRole)

        val newUser = InputUserUpdate(
            id = oldUser.id!!,
            email = oldUser.email,
            name = oldUser.name,
            surname = oldUser.surname,
            userRoleId = newRole.id!!
        )
        every { userRepository.findByIdOrNull(eq(oldUser.id!!)) } returns oldUser
        every { userRepository.findOneByEmail(eq(newUser.email)) } returns null

        if (role != ADMINISTRATOR) {
            val exception = assertThrows<I18nValidationException> { userService.update(newUser) }
            assertThat(exception.httpStatus).isEqualTo(HttpStatus.FORBIDDEN)
        }

        if (role == ADMINISTRATOR) {
            val result = userService.update(newUser)
            assertThat(result.userRole).isEqualTo(OutputUserRole(id = newRole.id, name = newRole.name))
        }
    }

    @Test
    fun update_emailTaken() {
        every { userRepository.save(any<User>()) } returnsArgument (0)

        val newUser = InputUserUpdate(
            id = oldUser.id!!,
            email = "already@taken.eu",
            name = "NewName",
            surname = "NewSurname",
            userRoleId = oldRole.id!!
        )
        every { userRepository.findByIdOrNull(eq(oldUser.id!!)) } returns oldUser
        every { userRepository.findOneByEmail(eq("already@taken.eu")) } returns User(id = 345, email = "", name = "", surname = "", userRole = UserRole(id = null, name = ""), password = "")

        val exception = assertThrows<I18nValidationException> { userService.update(newUser) }

        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(exception.i18nFieldErrors)
            .containsAllEntriesOf(mapOf("email" to I18nFieldError("user.email.not.unique")))
    }

    @Test
    fun changePassword_notExistingUser() {
        every { userRepository.findByIdOrNull(eq<Long>(-1)) } returns null
        assertThrows<ResourceNotFoundException> { userService.changePassword(-1, InputPassword("", null)) }
    }

    @Test
    fun changePassword_user_wrongOld() {
        every { securityService.currentUser } returns
            LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$APPLICANT_USER")))

        every { userRepository.findByIdOrNull(eq(oldUser.id!!)) } returns oldUser

        val inputPassword = InputPassword(
            password = "new_pass",
            oldPassword = "old_pass_wrong"
        )
        val exception = assertThrows<I18nValidationException> { userService.changePassword(oldUser.id!!, inputPassword) }
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(exception.i18nFieldErrors)
            .containsAllEntriesOf(mapOf("password" to I18nFieldError("user.password.not.match")))
    }

    @Test
    fun changePassword_user_ok() {
        every { securityService.currentUser } returns
            LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$APPLICANT_USER")))

        every { userRepository.findByIdOrNull(eq(oldUser.id!!)) } returns oldUser
        every { userRepository.save(any<User>()) } returnsArgument (0)

        val inputPassword = InputPassword(
            password = "new_pass",
            oldPassword = "old_pass"
        )

        userService.changePassword(oldUser.id!!, inputPassword)

        val event = slot<User>()
        verify { userRepository.save(capture(event)) }
        assertThat(passwordEncoder.matches("new_pass", event.captured.password))
    }

    @Test
    fun changePassword_admin() {
        every { securityService.currentUser } returns
            LocalCurrentUser(user, "hash_pass", listOf(SimpleGrantedAuthority("ROLE_$ADMINISTRATOR")))

        every { userRepository.findByIdOrNull(eq(oldUser.id!!)) } returns oldUser
        every { userRepository.save(any<User>()) } returnsArgument (0)

        userService.changePassword(oldUser.id!!, InputPassword("new_pass"))

        val event = slot<User>()
        verify { userRepository.save(capture(event)) }
        assertThat(passwordEncoder.matches("new_pass", event.captured.password))
    }

}
