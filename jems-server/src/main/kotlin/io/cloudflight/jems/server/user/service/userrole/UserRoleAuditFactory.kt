package io.cloudflight.jems.server.user.service.userrole

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermissionNode
import io.cloudflight.jems.server.user.service.model.UserRolePermissionNodeType

val DEFAULT_PROJECT_PERMISSIONS =
    UserRolePermissionNode(
        name = "Allow user to create/collaborate",
        editPermissions = setOf(UserRolePermission.ProjectCreate),
        type = UserRolePermissionNodeType.TOGGLE_SECTION,
    )

val DEFAULT_USER_INSPECT_PERMISSIONS =
    UserRolePermissionNode(
        name = "Allow user to monitor projects",
        type = UserRolePermissionNodeType.TOGGLE_SECTION,
        children = listOf(
            UserRolePermissionNode(
                name = "Contracting",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "Contract monitoring",
                        viewPermissions = setOf(UserRolePermission.ProjectContractingView),
                        editPermissions = setOf(UserRolePermission.ProjectSetToContracted),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    )
            )),
            UserRolePermissionNode(
                name = "Application form",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "Application form",
                        viewPermissions = setOf(UserRolePermission.ProjectFormRetrieve),
                        editPermissions = setOf(UserRolePermission.ProjectFormUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Application annexes",
                        viewPermissions = setOf(UserRolePermission.ProjectFileApplicationRetrieve),
                        editPermissions = setOf(UserRolePermission.ProjectFileApplicationUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Check & Submit",
                        viewPermissions = setOf(UserRolePermission.ProjectCheckApplicationForm),
                        editPermissions = setOf(UserRolePermission.ProjectSubmission),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Assessment & Decision",
                        type = UserRolePermissionNodeType.SECTION_HEADER,
                        children = listOf(
                            UserRolePermissionNode(
                                name = "Assessment & Decision panel",
                                viewPermissions = setOf(UserRolePermission.ProjectAssessmentView),
                                editPermissions = setOf(
                                    UserRolePermission.ProjectAssessmentQualityEnter,
                                    UserRolePermission.ProjectAssessmentEligibilityEnter,
                                    UserRolePermission.ProjectStatusDecideEligible,
                                    UserRolePermission.ProjectStatusDecideIneligible,
                                    UserRolePermission.ProjectStatusDecideApproved,
                                    UserRolePermission.ProjectStatusDecideApprovedWithConditions,
                                    UserRolePermission.ProjectStatusDecideNotApproved
                                ),
                                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Revert decision",
                                editPermissions = setOf(UserRolePermission.ProjectStatusDecisionRevert),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Return to applicant",
                                editPermissions = setOf(UserRolePermission.ProjectStatusReturnToApplicant),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Start step two",
                                editPermissions = setOf(UserRolePermission.ProjectStartStepTwo),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Annexes",
                                viewPermissions = setOf(UserRolePermission.ProjectFileAssessmentRetrieve),
                                editPermissions = setOf(UserRolePermission.ProjectFileAssessmentUpdate),
                                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                            ),
                        )
                    ),
                    UserRolePermissionNode(
                        name = "Modification",
                        type = UserRolePermissionNodeType.SECTION_HEADER,
                        children = listOf(
                            UserRolePermissionNode(
                                name = "Modification panel",
                                viewPermissions = setOf(UserRolePermission.ProjectModificationView),
                                editPermissions = setOf(
                                    UserRolePermission.ProjectStatusDecideModificationApproved,
                                    UserRolePermission.ProjectStatusDecideModificationNotApproved
                                ),
                                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                            ),

                            UserRolePermissionNode(
                                name = "Open modification",
                                editPermissions = setOf(UserRolePermission.ProjectOpenModification),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Modification files",
                                viewPermissions = setOf(UserRolePermission.ProjectModificationFileAssessmentRetrieve),
                                editPermissions = setOf(UserRolePermission.ProjectModificationFileAssessmentUpdate),
                                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                            ),
                        )
                    )
                )
            )
        )
    )


val DEFAULT_TOP_NAVIGATION_PERMISSIONS =
    UserRolePermissionNode(
        name = "Top navigation bar",
        type = UserRolePermissionNodeType.SECTION_HEADER,
        children = listOf(
            UserRolePermissionNode(
                name = "Dashboard",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "My applications",
                        viewPermissions = setOf(UserRolePermission.ProjectsWithOwnershipRetrieve),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW,
                    ),
                    UserRolePermissionNode(
                        name = "Open calls",
                        viewPermissions = setOf(UserRolePermission.CallPublishedRetrieve),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW,
                    ),
                )
            ),
            UserRolePermissionNode(
                name = "Applications",
                viewPermissions = setOf(UserRolePermission.ProjectRetrieve),
                editPermissions = setOf(UserRolePermission.ProjectRetrieveEditUserAssignments),
                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
            ),
            UserRolePermissionNode(
                name = "Calls",
                viewPermissions = setOf(UserRolePermission.CallRetrieve),
                editPermissions = setOf(UserRolePermission.CallUpdate),
                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
            ),
            UserRolePermissionNode(
                name = "Programme",
                viewPermissions = setOf(UserRolePermission.ProgrammeSetupRetrieve),
                editPermissions = setOf(UserRolePermission.ProgrammeSetupUpdate),
                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
            ),
            UserRolePermissionNode(
                name = "System",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "User management",
                        viewPermissions = setOf(UserRolePermission.UserRetrieve, UserRolePermission.RoleRetrieve),
                        editPermissions = setOf(
                            UserRolePermission.UserCreate,
                            UserRolePermission.UserUpdate,
                            UserRolePermission.UserUpdateRole,
                            UserRolePermission.RoleCreate,
                            UserRolePermission.RoleUpdate
                        ),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Audit log",
                        viewPermissions = setOf(UserRolePermission.AuditRetrieve),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW,
                    ),
                )
            )
        )
    )

fun userRoleCreated(context: Any, userRole: UserRole): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ROLE_PRIVILEGES_CREATED)
            .description("A new user role ${userRole.name} was created:\n${getRoleAsString(userRole)}")
            .build()
    )

fun userRoleUpdated(context: Any, userRole: UserRole, oldRoleName: String): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.ROLE_PRIVILEGES_CHANGED)
            .description(
                "The role $oldRoleName was changed to:\n${getRoleAsString(userRole)}"
            )
            .build()
    )

private fun getRoleAsString(role: UserRole): String {
    val auditString = StringBuilder()
    auditString.append("Role name: ${role.name}")
    val default = if (role.isDefault) "YES" else "NO"
    auditString.append("\nDefault role [$default]\n")

    appendToAudit(role.permissions, DEFAULT_PROJECT_PERMISSIONS, "", auditString)
    appendToAudit(role.permissions, DEFAULT_USER_INSPECT_PERMISSIONS, "", auditString)
    appendToAudit(role.permissions, DEFAULT_TOP_NAVIGATION_PERMISSIONS, "", auditString)
    return auditString.toString()
}

private fun appendToAudit(
    permissions: Set<UserRolePermission>,
    node: UserRolePermissionNode,
    indent: String,
    audit: StringBuilder
) {
    audit
        .append('\n', indent, if (node.children.isEmpty()) "" else '+', node.name)
        .append(" ", getAuditPermissionState(node, permissions))

    node.children.forEach { child ->
        appendToAudit(permissions, child, indent + '\t', audit)
    }
}

private fun getAuditPermissionState(node: UserRolePermissionNode, permissions: Set<UserRolePermission>): String {
    if (node.type == UserRolePermissionNodeType.HIDDEN_VIEW_EDIT || node.type == UserRolePermissionNodeType.HIDDEN_VIEW) {
        val edit = node.editPermissions.any { it in permissions }
        val view = node.viewPermissions.any { it in permissions }
        return if (edit) "[EDIT]" else if (view) "[VIEW]" else "[HIDE]"
    }
    if (node.type == UserRolePermissionNodeType.TOGGLE_EDIT) {
        val edit = node.editPermissions.any { it in permissions }
        return if (edit) "[ACTIVE]" else "[HIDE]"
    }
    if (node.type == UserRolePermissionNodeType.TOGGLE_SECTION) {
        return if (sectionIsChecked(node, permissions)) "[CHECKED]" else "[UNCHECKED]"
    }
    return ""
}

private fun sectionIsChecked(node: UserRolePermissionNode, permissions: Set<UserRolePermission>): Boolean {
    return node.editPermissions.any { it in permissions }
        || node.viewPermissions.any { it in permissions }
        || node.children.any { sectionIsChecked(it, permissions) }
}


