package io.cloudflight.jems.server.service

import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.server.user.entity.UserRole
import io.cloudflight.jems.server.user.repository.UserRoleRepository
import io.cloudflight.jems.server.user.service.UserRoleService
import io.cloudflight.jems.server.user.service.UserRoleServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class UserRoleServiceTest {

    private val UNPAGED = Pageable.unpaged()

    @MockK
    lateinit var userRoleRepository: UserRoleRepository

    lateinit var userRoleService: UserRoleService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        userRoleService = UserRoleServiceImpl(userRoleRepository)
    }

    @Test
    fun getAllAccountRoles() {
        val roleToReturn = UserRole(
                id = 85,
                name = "Name"
        )
        every { userRoleRepository.findAll(UNPAGED) } returns PageImpl(listOf(roleToReturn))

        val result = userRoleService.findAll(UNPAGED)

        assertThat(result.totalElements).isEqualTo(1);

        val expectedRoles = listOf(
            OutputUserRole(
                id = 85,
                name = "Name"
            )
        )
        assertThat(result.stream()).isEqualTo(expectedRoles)
    }

}
