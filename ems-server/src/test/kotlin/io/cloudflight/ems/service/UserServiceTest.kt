package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.stream.Collectors

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
            password = "hash_pass")
        every { userRepository.findAll(UNPAGED) } returns PageImpl(listOf(userToReturn))

        // test start
        val result = userService.getUsers(UNPAGED)

        // assertions:
        assertEquals(1, result.totalElements)

        val expectedUsers = listOf(OutputUser(
            id = 85,
            email = "admin@ems.io",
            name = "Name",
            surname = "Surname",
            userRole = OutputUserRole(9, "admin"))
        )
        assertIterableEquals(expectedUsers, result.get().collect(Collectors.toList()))
    }

    @Test
    fun getUser_empty() {
        every { userRepository.findByEmail(eq("not_existing@ems.io")) } returns null

        val result = userService.getByEmail("not_existing@ems.io")
        assertNull(result)
    }

    @Test
    fun getUser_OK() {
        every { userRepository.findByEmail(eq("admin@ems.io")) } returns
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
            userRole = OutputUserRole(2, "admin"))
        assertEquals(expectedUser, result)
    }

}
