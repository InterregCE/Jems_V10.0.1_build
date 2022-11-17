package io.cloudflight.jems.server.user.service.userrole.createUserRole

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.util.Optional

internal class CreateUserRoleTest : UnitTest() {

    companion object {
        private const val ROLE_ID = 5L

        private val userRoleCreate = UserRoleCreate(
            name = "maintainer",
            isDefault = false,
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )
        private val expectedUserRole = UserRole(
            id = ROLE_ID,
            name = "maintainer",
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )
    }

    @MockK
    lateinit var persistence: UserRolePersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var createUserRole: CreateUserRole

    @Test
    fun `createUserRole - OK`() {
        every { persistence.findUserRoleByName(userRoleCreate.name) } returns Optional.empty()
        every { persistence.create(any()) } returns expectedUserRole

        assertThat(createUserRole.createUserRole(userRoleCreate)).isEqualTo(expectedUserRole)
        verify(exactly = 1) { persistence.create(userRoleCreate) }
        verify(exactly = 1) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `createUserRole - permission combination invalid`() {
        val userRoleInvalid = userRoleCreate.copy(permissions = setOf(
            UserRolePermission.ProjectSubmission,
            UserRolePermission.ProjectAssessmentChecklistConsolidate /* this one requires ProjectAssessmentChecklistUpdate */,
        ))
        every { persistence.findUserRoleByName(userRoleInvalid.name) } returns Optional.empty()

        assertThrows<UserRolePermissionCombinationInvalid> { createUserRole.createUserRole(userRoleInvalid) }
    }

    @Test
    fun `createUserRole - name already taken`() {
        every { persistence.findUserRoleByName(userRoleCreate.name) } returns Optional.of(
            UserRoleSummary(
                id = 96L,
                name = userRoleCreate.name
            )
        )
        assertThrows<UserRoleNameAlreadyTaken> { createUserRole.createUserRole(userRoleCreate) }
    }

}
