package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.repository.AccountRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class AccountServiceTest {

    private val UNPAGED = Pageable.unpaged()

    @MockK
    lateinit var accountRepository: AccountRepository

    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        userService = UserServiceImpl(accountRepository)
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

}
