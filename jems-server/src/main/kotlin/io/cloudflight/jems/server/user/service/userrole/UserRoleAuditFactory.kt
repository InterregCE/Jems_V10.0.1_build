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
                name = "Reporting",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "Project reports",
                        viewPermissions = setOf(UserRolePermission.ProjectReportingProjectView),
                        editPermissions = setOf(UserRolePermission.ProjectReportingProjectEdit),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Partner reports",
                        type = UserRolePermissionNodeType.SECTION_HEADER,
                        children = listOf(
                            UserRolePermissionNode(
                                name = "Partner reports",
                                viewPermissions = setOf(UserRolePermission.ProjectReportingView),
                                editPermissions = setOf(UserRolePermission.ProjectReportingEdit),
                                type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Reopen partner report",
                                editPermissions = setOf(UserRolePermission.ProjectReportingReOpen),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Instantiate checklists after control finalized",
                                editPermissions = setOf(UserRolePermission.ProjectReportingChecklistAfterControl),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                            UserRolePermissionNode(
                                name = "Reopen control report",
                                editPermissions = setOf(UserRolePermission.ProjectPartnerControlReportingReOpen),
                                type = UserRolePermissionNodeType.TOGGLE_EDIT,
                            ),
                        )
                    ),
                )
            ),
            UserRolePermissionNode(
                name = "Contracting",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "Contract monitoring",
                        viewPermissions = setOf(UserRolePermission.ProjectContractingView),
                        editPermissions = setOf(UserRolePermission.ProjectSetToContracted),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Contracts and agreements",
                        viewPermissions = setOf(UserRolePermission.ProjectContractsView),
                        editPermissions = setOf(UserRolePermission.ProjectContractsEdit),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Project managers",
                        viewPermissions = setOf(UserRolePermission.ProjectContractingManagementView),
                        editPermissions = setOf(UserRolePermission.ProjectContractingManagementEdit),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Project reporting schedule",
                        viewPermissions = setOf(UserRolePermission.ProjectContractingReportingView),
                        editPermissions = setOf(UserRolePermission.ProjectContractingReportingEdit),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Partner details",
                        viewPermissions = setOf(UserRolePermission.ProjectContractingPartnerView),
                        editPermissions = setOf(UserRolePermission.ProjectContractingPartnerEdit),
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
                            UserRolePermissionNode(
                                name = "Assessment checklists",
                                type = UserRolePermissionNodeType.SECTION_HEADER,
                                children = listOf(
                                    UserRolePermissionNode(
                                        name = "Instantiate assessment checklists",
                                        editPermissions = setOf(UserRolePermission.ProjectAssessmentChecklistUpdate),
                                        type = UserRolePermissionNodeType.TOGGLE_EDIT,
                                    ),
                                    UserRolePermissionNode(
                                        name = "Consolidate assessment checklists",
                                        editPermissions = setOf(UserRolePermission.ProjectAssessmentChecklistConsolidate),
                                        type = UserRolePermissionNodeType.TOGGLE_EDIT,
                                    ),
                                    UserRolePermissionNode(
                                        name = "Visible checklists table",
                                        viewPermissions = setOf(UserRolePermission.ProjectAssessmentChecklistSelectedRetrieve),
                                        editPermissions = setOf(UserRolePermission.ProjectAssessmentChecklistSelectedUpdate),
                                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                                    ),
                                )
                            )
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
                    ),
                    UserRolePermissionNode(
                        name = "Project privileges",
                        viewPermissions = setOf(UserRolePermission.ProjectMonitorCollaboratorsRetrieve),
                        editPermissions = setOf(UserRolePermission.ProjectMonitorCollaboratorsUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Shared folder",
                        viewPermissions = setOf(UserRolePermission.ProjectMonitorSharedFolderView),
                        editPermissions = setOf(UserRolePermission.ProjectMonitorSharedFolderEdit),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
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
                        name = "Notifications",
                        viewPermissions = setOf(UserRolePermission.NotificationsRetrieve),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW,
                    ),
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
                name = "Payments",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "Payments to projects",
                        viewPermissions = setOf(UserRolePermission.PaymentsRetrieve),
                        editPermissions = setOf(UserRolePermission.PaymentsUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    ),
                    UserRolePermissionNode(
                        name = "Advance payments",
                        viewPermissions = setOf(UserRolePermission.AdvancePaymentsRetrieve),
                        editPermissions = setOf(UserRolePermission.AdvancePaymentsUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT,
                    )
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
                name = "Controller",
                type = UserRolePermissionNodeType.SECTION_HEADER,
                children = listOf(
                    UserRolePermissionNode(
                        name = "Institutions",
                        viewPermissions = setOf(UserRolePermission.InstitutionsRetrieve),
                        editPermissions = setOf(UserRolePermission.InstitutionsUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT
                    ),
                    UserRolePermissionNode(
                        name = "Unrestricted access to all institutions",
                        viewPermissions = setOf(UserRolePermission.InstitutionsUnlimited),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW
                    ),
                    UserRolePermissionNode(
                        name = "Assignment",
                        viewPermissions = setOf(UserRolePermission.InstitutionsAssignmentRetrieve),
                        editPermissions = setOf(UserRolePermission.InstitutionsAssignmentUpdate),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW_EDIT
                    ),
                    UserRolePermissionNode(
                        name = "Unrestricted access to all assignments",
                        viewPermissions = setOf(UserRolePermission.AssignmentsUnlimited),
                        type = UserRolePermissionNodeType.HIDDEN_VIEW
                    )
                )
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
        auditCandidate = AuditBuilder(AuditAction.ROLE_PRIVILEGES_CHANGED)
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


