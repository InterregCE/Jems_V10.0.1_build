package io.cloudflight.jems.server.controllerInstitution.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ControllerInstitutionAuthorizationTest: UnitTest() {

    companion object {
        private const val MONITOR_USER_ID = 3L
        private const val INSTITUTION_ONE_ID = 1L

        private val controllerInstitutionUser = Optional.of(
            ControllerInstitutionUser(
                institutionId = 1L,
                userId = MONITOR_USER_ID,
                userEmail = "testuser01@jems.eu",
                accessLevel = UserInstitutionAccessLevel.View
            )
        )
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var currentUser: CurrentUser

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var controllerInstitutionAuthorization: ControllerInstitutionAuthorization


    @BeforeEach
    fun resetMocks() {
        clearMocks(currentUser)
        clearMocks(securityService)
        every { securityService.currentUser } returns currentUser
    }

    @Test
    fun `limited view permission can retrieve controller institutions`() {
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns false
        Assertions.assertThat(controllerInstitutionAuthorization.canRetrieveControllerInstitutions()).isTrue
    }

    @Test
    fun `assigned user with limited view permission can view institution details`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns false
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns controllerInstitutionUser
        Assertions.assertThat(controllerInstitutionAuthorization.canViewControllerInstitutionDetails(INSTITUTION_ONE_ID)).isTrue
    }

    @Test
    fun `user with limited view permission can NOT view other institution details`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns false
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns Optional.empty()
        Assertions.assertThat(controllerInstitutionAuthorization.canViewControllerInstitutionDetails(INSTITUTION_ONE_ID)).isFalse
    }

    @Test
    fun `assigned user with limited edit permission can update institution details`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns false
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns controllerInstitutionUser
        Assertions.assertThat(controllerInstitutionAuthorization.canUpdateControllerInstitution(INSTITUTION_ONE_ID)).isTrue
    }

    @Test
    fun `user with limited edit permission can NOT update other institution details`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns false
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns Optional.empty()
        Assertions.assertThat(controllerInstitutionAuthorization.canUpdateControllerInstitution(INSTITUTION_ONE_ID)).isFalse
    }

    @Test
    fun `user with unlimited edit permission can update any institution details`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns true
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns Optional.empty()
        Assertions.assertThat(controllerInstitutionAuthorization.canUpdateControllerInstitution(INSTITUTION_ONE_ID)).isTrue
    }

    @Test
    fun `user with unlimited and NO edit permission can NOT update institution details`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns true
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns Optional.empty()
        Assertions.assertThat(controllerInstitutionAuthorization.canUpdateControllerInstitution(INSTITUTION_ONE_ID)).isFalse
    }

    @Test
    fun `user with unlimited and edit permission can create institution`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns true
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns Optional.empty()
        Assertions.assertThat(controllerInstitutionAuthorization.canCreateControllerInstitution()).isTrue
    }

    @Test
    fun `user with unlimited and view permission can NOT create institution`(){
        every { currentUser.hasPermission(UserRolePermission.InstitutionsRetrieve) } returns true
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUpdate) } returns false
        every { currentUser.hasPermission(UserRolePermission.InstitutionsUnlimited) } returns true
        every { securityService.getUserIdOrThrow() } returns MONITOR_USER_ID
        every {
            controllerInstitutionPersistence.getInstitutionUserByInstitutionIdAndUserId(
                INSTITUTION_ONE_ID,
                MONITOR_USER_ID
            )
        } returns Optional.empty()
        Assertions.assertThat(controllerInstitutionAuthorization.canCreateControllerInstitution()).isFalse
    }

}
