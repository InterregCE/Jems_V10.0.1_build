package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class UserServiceTest {

    private val UNPAGED = Pageable.unpaged()

    @MockK
    lateinit var userRepository: UserRepository

    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        userService = UserServiceImpl(userRepository)
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
                surname = "Surname",
                userRole = OutputUserRole(9, "admin")
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
    fun getUser_OK() {
        every { userRepository.findOneByEmail(eq("admin@ems.io")) } returns
                User(
                    id = 50,
                    email = "admin@ems.io",
                    name = "name",
                    surname = "surname",
                    userRole = UserRole(2, "admin"),
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
