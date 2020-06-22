package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputUser
import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationError
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

class AccountServiceTest {

    private val UNPAGED = Pageable.unpaged()

    private val user = OutputUser(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    @MockK
    lateinit var accountRepository: AccountRepository
    @MockK
    lateinit var accountRoleRepository: AccountRoleRepository
    @MockK
    lateinit var auditService: AuditService
    @MockK
    lateinit var securityService: SecurityService

    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        every { auditService.logEvent(any()) } answers {} // doNothing
        userService = UserServiceImpl(accountRepository, accountRoleRepository, auditService, securityService)
    }

    @Test
    fun getAllUsers() {
        val userToReturn = Account(
            id = 85,
            email = "admin@ems.io",
            name = "Name",
            surname = "Surname",
            accountRole = AccountRole(9, "admin"),
            password = "hash_pass"
        )
        every { accountRepository.findAll(UNPAGED) } returns PageImpl(listOf(userToReturn))

        // test start
        val result = userService.findAll(UNPAGED)

        // assertions:
        assertThat(result.totalElements).isEqualTo(1);

        val expectedUsers = listOf(
            OutputUser(
                id = 85,
                email = "admin@ems.io",
                name = "Name",
                surname = "Surname",
                userRole = OutputUserRole(9, "admin")
            )
        )
        assertThat(result.stream()).isEqualTo(expectedUsers);
    }

    @Test
    fun getUser_empty() {
        every { accountRepository.findOneByEmail(eq("not_existing@ems.io")) } returns null

        val result = userService.findOneByEmail("not_existing@ems.io")
        assertThat(result).isNull();
    }

    @Test
    fun getUser_OK() {
        every { accountRepository.findOneByEmail(eq("admin@ems.io")) } returns
                Account(
                    id = 50,
                    email = "admin@ems.io",
                    name = "name",
                    surname = "surname",
                    accountRole = AccountRole(2, "admin"),
                    password = "hash_pass"
                )

        val result = userService.getByEmail("admin@ems.io")

        val expectedUser = OutputUser(
            id = 50,
            email = "admin@ems.io",
            name = "name",
            surname = "surname",
            userRole = OutputUserRole(2, "admin")
        )
        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    fun saveUser_wrong() {
        every { accountRepository.findOneByEmail(eq("existing@user.com")) } returns
            Account(1, "", "", "", AccountRole(1, ""), "")
        every { accountRoleRepository.findById(eq(10)) } returns Optional.empty()

        val account = InputUser(
            email = "existing@user.com",
            name = "Ondrej",
            surname = "Tester",
            accountRoleId = 10 // does not exist
        )

        val exception = assertThrows<I18nValidationError> { userService.create(account) }
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.httpStatus)

        val expectedErrors = mapOf(
            "accountRoleId" to I18nFieldError("user.accountRoleId.does.not.exist"),
            "email" to I18nFieldError("user.email.not.unique")
        )
        assertThat(exception.i18nFieldErrors).isEqualTo(expectedErrors)
    }

    @Test
    fun saveUser_OK() {
        every { accountRepository.findOneByEmail(eq("new@user.com")) } returns null
        every { accountRoleRepository.findById(eq(54)) } returns Optional.of(AccountRole(54, "admin_role"))
        every { accountRepository.save(any<Account>()) } returnsArgument(0)

        val account = InputUser(
            email = "new@user.com",
            name = "Ondrej",
            surname = "Tester",
            accountRoleId = 54
        )

        val result = userService.create(account)
        assertEquals("new@user.com", result.email)
        assertEquals("Ondrej", result.name)
        assertEquals("Tester", result.surname)
        assertEquals(OutputUserRole(54, "admin_role"), result.userRole)

        val event = slot<Audit>()
        verify { auditService.logEvent(capture(event)) }
        with(event.captured) {
            assertEquals("admin@admin.dev", username)
            assertEquals(AuditAction.USER_CREATED, action)
            assertEquals("new user new@user.com with role admin_role has been created by admin@admin.dev", description)
        }

    }

}
