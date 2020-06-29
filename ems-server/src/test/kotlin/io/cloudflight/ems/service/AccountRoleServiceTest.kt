package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.repository.AccountRoleRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class AccountRoleServiceTest {

    private val UNPAGED = Pageable.unpaged()

    @MockK
    lateinit var accountRoleRepository: AccountRoleRepository

    lateinit var accountRoleService: AccountRoleService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        accountRoleService = AccountRoleServiceImpl(accountRoleRepository)
    }

    @Test
    fun getAllAccountRoles() {
        val roleToReturn = AccountRole(
            id = 85,
            name = "Name"
        )
        every { accountRoleRepository.findAll(UNPAGED) } returns PageImpl(listOf(roleToReturn))

        val result = accountRoleService.findAll(UNPAGED)

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
