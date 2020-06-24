package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputAccount
import io.cloudflight.ems.api.dto.OutputAccount
import io.cloudflight.ems.api.dto.OutputAccountRole
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
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

class AccountServiceTest {

    private val UNPAGED = Pageable.unpaged()

    private val user = OutputAccount(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        accountRole = OutputAccountRole(id = 1, name = "ADMIN")
    )

    @MockK
    lateinit var accountRepository: AccountRepository
    @MockK
    lateinit var accountRoleRepository: AccountRoleRepository
    @RelaxedMockK
    lateinit var auditService: AuditService
    @MockK
    lateinit var securityService: SecurityService

    lateinit var accountService: AccountService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        accountService = AccountServiceImpl(accountRepository, accountRoleRepository, auditService, securityService)
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
        val result = accountService.findAll(UNPAGED)

        // assertions:
        assertThat(result.totalElements).isEqualTo(1);

        val expectedUsers = listOf(
            OutputAccount(
                id = 85,
                email = "admin@ems.io",
                name = "Name",
                surname = "Surname",
                accountRole = OutputAccountRole(9, "admin")
            )
        )
        assertThat(result.stream()).isEqualTo(expectedUsers);
    }

    @Test
    fun getUser_empty() {
        every { accountRepository.findOneByEmail(eq("not_existing@ems.io")) } returns null

        val result = accountService.findOneByEmail("not_existing@ems.io")
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

        val result = accountService.getByEmail("admin@ems.io")

        val expectedUser = OutputAccount(
            id = 50,
            email = "admin@ems.io",
            name = "name",
            surname = "surname",
            accountRole = OutputAccountRole(2, "admin")
        )
        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    fun saveUser_wrong() {
        every { accountRepository.findOneByEmail(eq("existing@user.com")) } returns
            Account(1, "", "", "", AccountRole(1, ""), "")
        every { accountRoleRepository.findById(eq(10)) } returns Optional.empty()

        val account = InputAccount(
            email = "existing@user.com",
            name = "Ondrej",
            surname = "Tester",
            accountRoleId = 10 // does not exist
        )

        val exception = assertThrows<I18nValidationError> { accountService.create(account) }
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

        val account = InputAccount(
            email = "new@user.com",
            name = "Ondrej",
            surname = "Tester",
            accountRoleId = 54
        )

        val result = accountService.create(account)
        assertEquals("new@user.com", result.email)
        assertEquals("Ondrej", result.name)
        assertEquals("Tester", result.surname)
        assertEquals(OutputAccountRole(54, "admin_role"), result.accountRole)

        val event = slot<Audit>()
        verify { auditService.logEvent(capture(event)) }
        with(event.captured) {
            assertEquals("admin@admin.dev", username)
            assertEquals(AuditAction.USER_CREATED, action)
            assertEquals("new user new@user.com with role admin_role has been created by admin@admin.dev", description)
        }

    }

}
