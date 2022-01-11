package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieveEditUserAssignments
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFormRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileApplicationRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectCheckApplicationForm
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectAssessmentView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectContractingView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusDecisionRevert
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStatusReturnToApplicant
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectStartStepTwo
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectModificationFileAssessmentRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectModificationView
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectOpenModification
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectSetToContracted
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectSubmission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetUserTest : UnitTest() {

    companion object {
        private const val USER_ID = 40L

        private val userRole = UserRole(
            id = 9L,
            name = "maintainer",
            permissions = setOf(ProjectSubmission)
        )

        private val user = User(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = userRole,
            userStatus = UserStatus.ACTIVE
        )
        private val userSummary = UserSummary(
            id = USER_ID,
            email = user.email,
            name = user.name,
            surname = user.surname,
            userRole = UserRoleSummary(id = userRole.id, name = userRole.name),
            userStatus = UserStatus.ACTIVE
        )

        private val userWithPassword = UserWithPassword(
            id = USER_ID,
            email = user.email,
            name = user.name,
            surname = user.surname,
            userRole = user.userRole,
            encodedPassword = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )
    }

    @MockK
    lateinit var persistence: UserPersistence

    @MockK
    lateinit var userRolePersistence: UserRolePersistence

    @InjectMockKs
    lateinit var getUser: GetUser

    @BeforeEach
    fun reset() {
        clearMocks(persistence)
        clearMocks(userRolePersistence)
    }

    @Test
    fun getUsers() {
        every { persistence.findAll(any(), null) } returns PageImpl(listOf(userSummary))
        assertThat(getUser.getUsers(Pageable.unpaged(), null).content).containsExactly(userSummary)
    }

    @Test
    fun getUsersWithProjectRetrievePermissions() {
        val toHaveSlot = slot<Set<UserRolePermission>>()
        val toNotHaveSlot = slot<Set<UserRolePermission>>()

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = capture(toHaveSlot),
            needsNotToHaveAnyOf = capture(toNotHaveSlot),
        ) } returns setOf(841L, 842L, 843L)

        val roleIdsSlot = slot<Set<Long>>()
        every { persistence.findAllWithRoleIdIn(capture(roleIdsSlot)) } returns listOf(userSummary)

        // TEST
        assertThat(getUser.getUsersWithProjectRetrievePermissions()).containsExactly(userSummary)

        assertThat(toHaveSlot.captured).containsExactly(ProjectRetrieve, ProjectRetrieveEditUserAssignments)
        assertThat(toNotHaveSlot.captured).isEmpty()

        assertThat(roleIdsSlot.captured).containsExactly(841L, 842L, 843L)
    }

    @Test
    fun getMonitorUsers() {
        val toHaveSlot = slot<Set<UserRolePermission>>()
        val toNotHaveSlot = slot<Set<UserRolePermission>>()

        every { userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = capture(toHaveSlot),
            needsNotToHaveAnyOf = capture(toNotHaveSlot),
        ) } returns setOf(941L, 942L, 943L)

        val roleIdsSlot = slot<Set<Long>>()
        every { persistence.findAllWithRoleIdIn(capture(roleIdsSlot)) } returns listOf(userSummary)

        // TEST
        assertThat(getUser.getMonitorUsers()).containsExactly(userSummary)

        assertThat(toHaveSlot.captured).containsExactlyInAnyOrder(
            ProjectFormRetrieve, ProjectFileApplicationRetrieve, ProjectCheckApplicationForm, ProjectAssessmentView,
            ProjectStatusDecisionRevert, ProjectStatusReturnToApplicant, ProjectStartStepTwo, ProjectFileAssessmentRetrieve,
            ProjectModificationView, ProjectOpenModification, ProjectModificationFileAssessmentRetrieve, ProjectContractingView, ProjectSetToContracted
        )
        assertThat(toNotHaveSlot.captured).containsExactly(ProjectRetrieve, ProjectRetrieveEditUserAssignments)

        assertThat(roleIdsSlot.captured).containsExactly(941L, 942L, 943L)
    }

    @Test
    fun getUserById() {
        every { persistence.getById(USER_ID) } returns userWithPassword
        assertThat(getUser.getUserById(USER_ID)).isEqualTo(user)
    }

}
