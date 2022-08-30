package io.cloudflight.jems.server.controllerInstitution.service.update_controller

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.INSTITUTION_ID
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_1_EMAIL
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_1_ID
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_2_EMAIL
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_2_ID
import io.cloudflight.jems.server.controllerInstitution.institutionUsers
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionValidator
import io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment.CheckInstitutionPartnerAssignments
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.AssignUsersToInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution.UpdateController
import io.cloudflight.jems.server.controllerInstitution.userSummaries
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class UpdateControllerTest : UnitTest() {

    companion object {

        private val createdAt = ZonedDateTime.now()
        private val institution = ControllerInstitution(
            id = INSTITUTION_ID,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = institutionUsers,
            createdAt = createdAt
        )

        private val institutionWithUsers = UpdateControllerInstitution(
            id = INSTITUTION_ID,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = institutionUsers.toList(),
            createdAt = createdAt
        )

    }


    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var userPersistence: UserPersistence

    @RelaxedMockK
    lateinit var userRolePersistence: UserRolePersistence

    @RelaxedMockK
    lateinit var userProjectPersistence: UserProjectPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var controllerInstitutionValidator: ControllerInstitutionValidator

    @MockK
    lateinit var checkInstitutionPartnerAssignments: CheckInstitutionPartnerAssignments

    @InjectMockKs
    lateinit var updateController: UpdateController

    @BeforeEach
    fun resetMocks() {
        clearMocks(controllerInstitutionPersistence)
        clearMocks(userPersistence)
        clearMocks(userProjectPersistence)
    }


    @Test
    fun updateInstitution() {
        every { userPersistence.findAllByEmails(institutionUsers.map { it.userEmail }) } returns userSummaries
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)
        every { controllerInstitutionPersistence.getInstitutionUsersByInstitutionId(INSTITUTION_ID) } returns institutionUsers.toList()
        every {
            controllerInstitutionPersistence.updateControllerInstitutionUsers(
                INSTITUTION_ID,
                institutionUsers, emptySet()
            )
        } returns institutionUsers.toSet()
        every { controllerInstitutionPersistence.updateControllerInstitution(institutionWithUsers) } returns institution

        assertThat(updateController.updateControllerInstitution(INSTITUTION_ID, institutionWithUsers))
            .isEqualTo(institution)
    }

    @Test
    fun `updateInstitution throw invalid user exception`() {
        val user1Summary = UserSummary(
            id = MONITOR_USER_1_ID,
            email = MONITOR_USER_1_EMAIL,
            name = "user1",
            surname = "",
            userRole = UserRoleSummary(4, "Controller"),
            userStatus = UserStatus.ACTIVE
        )
        every { controllerInstitutionPersistence.updateControllerInstitution(institutionWithUsers) } returns institution
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)
        every { userPersistence.findAllByEmails(institutionUsers.map { it.userEmail }) } returns listOf(user1Summary)
        assertThrows<AssignUsersToInstitutionException> {
            updateController.updateControllerInstitution(
                INSTITUTION_ID,
                institutionWithUsers
            )
        }
    }

    @Test
    fun `deleted institution user is removed from project assignment`() {
        val institutionUser1 = ControllerInstitutionUser(
            INSTITUTION_ID,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )

        val institutionUser1Summary = UserSummary(
            id = MONITOR_USER_1_ID,
            email = MONITOR_USER_1_EMAIL,
            name = "user1",
            surname = "",
            userRole = UserRoleSummary(4, "Controller"),
            userStatus = UserStatus.ACTIVE
        )

        val institutionUser2 = ControllerInstitutionUser(
            INSTITUTION_ID,
            MONITOR_USER_2_ID,
            MONITOR_USER_2_EMAIL,
            UserInstitutionAccessLevel.Edit
        )

        val updateControllerInstitution = UpdateControllerInstitution(
            id = 1L,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = listOf(institutionUser1),
            createdAt = createdAt
        )

        val institutionPartnerAssignment = InstitutionPartnerAssignment(
            institutionId = 1L,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        val institutionPartnerAssignmentsWithUsers = listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = 1L,
                userId = MONITOR_USER_1_ID,
                partnerProjectId = 1L
            ), InstitutionPartnerAssignmentWithUsers(
                institutionId = 1L,
                userId = MONITOR_USER_2_ID,
                partnerProjectId = 1L
            )
        )

        every { userPersistence.findAllByEmails(listOf(institutionUser1.userEmail)) } returns listOf(
            institutionUser1Summary
        )
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)
        every { controllerInstitutionPersistence.getInstitutionUsersByInstitutionId(institutionUser1.institutionId) } returns listOf(
            institutionUser1,
            institutionUser2
        )
        every {
            controllerInstitutionPersistence.updateControllerInstitutionUsers(
                institutionId = INSTITUTION_ID,
                usersToUpdate = emptySet(),
                usersIdsToDelete = setOf(institutionUser2.userId)
            )
        } returns setOf(institutionUser1)

        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByInstitutionId(INSTITUTION_ID) } returns listOf(
            institutionPartnerAssignment
        )
        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(
                setOf(
                    INSTITUTION_ID
                )
            )
        } returns institutionPartnerAssignmentsWithUsers

        every {
            userProjectPersistence.changeUsersAssignedToProject(
                1L,
                userIdsToAssign = emptySet(),
                userIdsToRemove = setOf(MONITOR_USER_2_ID)
            )
        } returns setOf(MONITOR_USER_1_ID)

        every { controllerInstitutionPersistence.updateControllerInstitution(updateControllerInstitution) } returns
            ControllerInstitution(
                id = INSTITUTION_ID,
                name = updateControllerInstitution.name,
                description = updateControllerInstitution.description,
                institutionNuts = emptyList(),
                institutionUsers = mutableSetOf(institutionUser1),
                createdAt = updateControllerInstitution.createdAt
            )

        assertThat(
            updateController.updateControllerInstitution(INSTITUTION_ID, updateControllerInstitution).institutionUsers
        ).containsExactly(institutionUser1)

        assertThat(
            userProjectPersistence.changeUsersAssignedToProject(
                1L,
                userIdsToAssign = emptySet(),
                userIdsToRemove = setOf(institutionUser2.userId)
            )
        ).doesNotContain(MONITOR_USER_2_ID)

    }

    @Test
    fun `deleted institution user is NOT removed from project assignment if he is part of a different institution assigned to same project`() {
        val institutionPartnerAssignment1 = InstitutionPartnerAssignment(
            institutionId = 1L,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        val institutionPartnerAssignment2 = InstitutionPartnerAssignment(
            institutionId = 2L,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        val institution1User = ControllerInstitutionUser(
            1L,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )
        val institution1UserSummary = UserSummary(
            id = MONITOR_USER_1_ID,
            email = MONITOR_USER_1_EMAIL,
            name = "user1",
            surname = "",
            userRole = UserRoleSummary(4, "Controller"),
            userStatus = UserStatus.ACTIVE
        )
        val institutionPartnerAssignmentWithUsers = listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = 1L,
                userId = MONITOR_USER_1_ID,
                partnerProjectId = 1L
            ), InstitutionPartnerAssignmentWithUsers(
                institutionId = 2L,
                userId = MONITOR_USER_1_ID,
                partnerProjectId = 1L
            )
        )

        val institutionsPartnerAssignments = listOf(institutionPartnerAssignment1, institutionPartnerAssignment2)
        val userSummaries = listOf(institution1UserSummary)

        val updateControllerInstitution = UpdateControllerInstitution(
            id = 1L,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = emptyList(),
            createdAt = createdAt
        )
        every { userPersistence.findAllByEmails(listOf(institution1User.userEmail)) } returns userSummaries
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)
        every { controllerInstitutionPersistence.getInstitutionUsersByInstitutionId(INSTITUTION_ID) } returns listOf(institution1User)
        every {
            controllerInstitutionPersistence.updateControllerInstitutionUsers(
                institutionId = 1L,
                usersIdsToDelete = setOf(institution1User.userId),
                usersToUpdate = emptySet()
            )
        } returns institutionUsers.toSet()

        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByInstitutionId(INSTITUTION_ID) } returns institutionsPartnerAssignments
        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(
                institutionsPartnerAssignments.map { it.partnerProjectId }.toSet()
            )
        } returns institutionPartnerAssignmentWithUsers
        every {
            userProjectPersistence.changeUsersAssignedToProject(
                1L,
                userIdsToAssign = emptySet(),
                userIdsToRemove = emptySet()
            )
        } returns setOf(institution1User.userId)
        every { checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedInstitution(INSTITUTION_ID) } just Runs

        assertThat(updateController.updateControllerInstitution(INSTITUTION_ID, updateControllerInstitution).institutionUsers)
            .isEmpty()

        assertThat(
            userProjectPersistence.changeUsersAssignedToProject(
                1L,
                userIdsToAssign = emptySet(),
                userIdsToRemove = emptySet()
            )
        ).containsExactly(institution1User.userId)

    }

    @Test
    fun `User added to institution is also assigned to partner project of institution assignment`() {

        val institutionAssignments = listOf(
            InstitutionPartnerAssignment(
                institutionId = INSTITUTION_ID,
                partnerId = 1L,
                partnerProjectId = 3L
            ),
            InstitutionPartnerAssignment(
                institutionId = INSTITUTION_ID,
                partnerId = 1L,
                partnerProjectId = 1L
            )
        )

        val institution1User = ControllerInstitutionUser(
            INSTITUTION_ID,
            MONITOR_USER_1_ID,
            MONITOR_USER_1_EMAIL,
            UserInstitutionAccessLevel.View
        )

        val institution2User = ControllerInstitutionUser(
            INSTITUTION_ID,
            MONITOR_USER_2_ID,
            MONITOR_USER_2_EMAIL,
            UserInstitutionAccessLevel.Edit
        )
        val userSummaries = listOf(
            UserSummary(
                id = MONITOR_USER_1_ID,
                email = MONITOR_USER_1_EMAIL,
                name = "user1",
                surname = "",
                userRole = UserRoleSummary(4, "Controller"),
                userStatus = UserStatus.ACTIVE
            ),
            UserSummary(
                id = MONITOR_USER_2_ID,
                email = MONITOR_USER_2_EMAIL,
                name = "user2",
                surname = "",
                userRole = UserRoleSummary(4, "Controller"),
                userStatus = UserStatus.ACTIVE
            )
        )

        val institutionPartnerAssignmentWithUsers = listOf(
            InstitutionPartnerAssignmentWithUsers(
                institutionId = INSTITUTION_ID,
                userId = MONITOR_USER_1_ID,
                partnerProjectId = 1L
            ),
            InstitutionPartnerAssignmentWithUsers(
                institutionId = INSTITUTION_ID,
                userId = MONITOR_USER_1_ID,
                partnerProjectId = 3L
            )
        )

        val updateControllerInstitution = UpdateControllerInstitution(
            id = 1L,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = listOf(institution1User, institution2User),
            createdAt = createdAt
        )

        every { userPersistence.findAllByEmails(listOf(institution1User.userEmail, institution2User.userEmail)) } returns userSummaries
        every {
            userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
                UserRolePermission.getProjectMonitorPermissions(),
                emptySet()
            )
        } returns setOf(1, 2, 4, 6)

        every { controllerInstitutionPersistence.getInstitutionUsersByInstitutionId(INSTITUTION_ID) } returns listOf(institution1User)

        every {
            controllerInstitutionPersistence.updateControllerInstitutionUsers(
                institutionId = 1L,
                usersIdsToDelete = emptySet(),
                usersToUpdate = setOf(institution1User, institution2User)
            )
        } returns institutionUsers.toSet()

        every { controllerInstitutionPersistence.getInstitutionPartnerAssignmentsByInstitutionId(INSTITUTION_ID) } returns institutionAssignments
        every {
            controllerInstitutionPersistence.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(
                institutionAssignments.map { it.partnerProjectId }.toSet()
            )
        } returns institutionPartnerAssignmentWithUsers

        every {
            userProjectPersistence.changeUsersAssignedToProject(
                1L,
                userIdsToAssign = setOf(institution2User.userId),
                userIdsToRemove = emptySet()
            )
        } returns setOf(institution1User.userId, institution2User.userId)

        every {
            userProjectPersistence.changeUsersAssignedToProject(
                3L,
                userIdsToAssign = setOf(institution2User.userId),
                userIdsToRemove = emptySet()
            )
        } returns setOf(institution1User.userId, institution2User.userId)

        every { checkInstitutionPartnerAssignments.checkInstitutionAssignmentsToRemoveForUpdatedInstitution(INSTITUTION_ID) } just Runs

        every { controllerInstitutionPersistence.updateControllerInstitution(updateControllerInstitution) } returns
            ControllerInstitution(
                id = INSTITUTION_ID,
                name = updateControllerInstitution.name,
                description = updateControllerInstitution.description,
                institutionNuts = emptyList(),
                institutionUsers = mutableSetOf(),
                createdAt = updateControllerInstitution.createdAt
            )

        assertThat(updateController.updateControllerInstitution(INSTITUTION_ID, updateControllerInstitution).institutionUsers).containsExactly(
            institution1User, institution2User
        )

        assertThat(
            userProjectPersistence.changeUsersAssignedToProject(
                1L,
                userIdsToAssign = setOf(institution2User.userId),
                userIdsToRemove = emptySet()
            )
        ).containsExactly(institution1User.userId, institution2User.userId)

        assertThat(
            userProjectPersistence.changeUsersAssignedToProject(
                3L,
                userIdsToAssign = setOf(institution2User.userId),
                userIdsToRemove = emptySet()
            )
        ).containsExactly(institution1User.userId, institution2User.userId)
    }
}
