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
        assertThat(auditDescription).contains("+Reporting")
        assertThat(auditDescription).contains("+Contracting")
        assertThat(auditDescription).contains("+Application form")
        assertThat(auditDescription).contains("Application form [VIEW]")
        assertThat(auditDescription).contains("Application annexes [EDIT]")
        assertThat(auditDescription).contains("Check & Submit [HIDE]")
        assertThat(auditDescription).contains("+Assessment & Decision")
        assertThat(auditDescription).contains("+Modification")
        assertThat(auditDescription).contains("Project privileges [HIDE]")
        assertThat(auditDescription).contains("Shared folder [HIDE]")

        auditDescription = userRoleCreated("", userRole).auditCandidate.description
        assertThat(auditDescription).contains("A new user role user role was created:")
        assertThat(auditDescription).contains("Default role [YES]")
    }


    @Test
    fun `programme user default permissions - shows correct audit message `() {
        val userRole = UserRole(
            id = 2L,
            name = "programme user",
            isDefault = true,
            permissions = UserRolePermission.programmeUserRoleDefaultPermissions
        )

        val expectedAuditMessage = """
            The role programme user was changed to:
            Role name: programme user
            Default role [YES]
            
            +Allow user to create/collaborate [UNCHECKED]
            +Allow user to monitor projects [CHECKED]
                +Reporting 
                    +Project reports 
                    +Partner reports 
                        Partner reports [VIEW]
                        Reopen partner report [HIDE]
                        Instantiate checklists after control finalized [HIDE]
                        Reopen control report [HIDE]
                +Contracting 
                    Contract monitoring [EDIT]
                    Contracts and agreements [EDIT]
                    Project managers [VIEW]
                    Project reporting schedule [VIEW]
                    +Partner details [VIEW]
                        State aid [HIDE]
                +Application form 
                    Application form [VIEW]
                    Application annexes [VIEW]
                    Check & Submit [VIEW]
                    +Assessment & Decision 
                        Assessment & Decision panel [EDIT]
                        Revert decision [HIDE]
                        Return to applicant [ACTIVE]
                        Start step two [ACTIVE]
                        Annexes [EDIT]
                        +Assessment checklists 
                            Instantiate assessment checklists [ACTIVE]
                            Consolidate assessment checklists [HIDE]
                            Visible checklists table [VIEW]
                    +Modification 
                        Modification panel [EDIT]
                        Open modification [ACTIVE]
                        Modification files [EDIT]
                    Project privileges [EDIT]
                    Shared folder [EDIT]
            +Top navigation bar 
                +Dashboard 
                    My applications [VIEW]
                    Call list [VIEW]
                    Notifications [HIDE]
                +Payments 
                    Payments to projects [VIEW]
                    Advance payments [VIEW]
                Applications [VIEW]
                Calls [VIEW]
                +Programme 
                    Programme setup [VIEW]
                    Data export [VIEW]
                +Controller 
                    Institutions [VIEW]
                    Unrestricted access to all institutions [HIDE]
                    Assignment [VIEW]
                    Unrestricted access to all assignments [HIDE]
                +System 
                    User management [HIDE]
                    Audit log [VIEW]
        """
        var auditDescription = userRoleUpdated("", userRole, "programme user").auditCandidate.description
        assertThat(auditDescription).isEqualToIgnoringWhitespace(expectedAuditMessage)

    }
}
