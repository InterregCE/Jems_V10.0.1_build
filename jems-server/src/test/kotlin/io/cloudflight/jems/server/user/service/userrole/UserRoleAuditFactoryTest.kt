package io.cloudflight.jems.server.user.service.userrole

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UserRoleAuditFactoryTest : UnitTest() {

    @Test
    fun `user role audit messages include permissions`() {
        val userRole = UserRole(
            id = 2L,
            name = "user role",
            isDefault = true,
            permissions = setOf(UserRolePermission.ProjectFileApplicationUpdate, UserRolePermission.ProjectFormRetrieve)
        )

        var auditDescription = userRoleUpdated("", userRole, "old role name").auditCandidate.description
        assertThat(auditDescription).contains("The role old role name was changed to:")
        assertThat(auditDescription).contains("Role name: user role")
        assertThat(auditDescription).contains("Allow user to create/collaborate [UNCHECKED]")
        assertThat(auditDescription).contains("+Allow user to monitor projects [CHECKED]")
        assertThat(auditDescription).contains("Assessment & Decision panel [HIDE]")
        assertThat(auditDescription).contains("Application form [VIEW]")
        assertThat(auditDescription).contains("Application annexes [EDIT]")

        auditDescription = userRoleCreated("", userRole).auditCandidate.description
        assertThat(auditDescription).contains("A new user role user role was created:")
        assertThat(auditDescription).contains("Default role [YES")
    }
}
