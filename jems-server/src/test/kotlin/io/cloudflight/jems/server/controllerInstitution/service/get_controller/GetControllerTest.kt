package io.cloudflight.jems.server.controllerInstitution.service.get_controller

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.ControllerInstitutionAuthorization
import io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution.GetController
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime


class GetControllerTest: UnitTest() {

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var controllerInstitutionAuthorization: ControllerInstitutionAuthorization

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getController: GetController


    companion object {
        private const val INSTITUTION_ID = 1L
        private val controllerInstitutionList = ControllerInstitutionList(
            id = INSTITUTION_ID,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            createdAt = ZonedDateTime.now()
        )
        private val controllerInstitution = ControllerInstitution(
            id = INSTITUTION_ID,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = mutableSetOf(),
            createdAt = ZonedDateTime.now()
        )
    }



    @Test
    fun getAllInstitutions() {
        every { controllerInstitutionAuthorization.hasPermission(UserRolePermission.InstitutionsUnlimited)} returns true
        every {controllerInstitutionPersistence.getControllerInstitutions(any())} returns PageImpl(listOf(controllerInstitutionList))
        assertThat(getController.getControllers(Pageable.unpaged()).content).containsExactly(controllerInstitutionList)
    }

    @Test
    fun getInstitutionsForUserWithLimitedAccess() {
        every { controllerInstitutionAuthorization.hasPermission(UserRolePermission.InstitutionsUnlimited)} returns false
        every { controllerInstitutionAuthorization.hasPermission(UserRolePermission.InstitutionsRetrieve)} returns true
        every { securityService.getUserIdOrThrow() } returns 2L
        every {controllerInstitutionPersistence.getControllerInstitutionsByUserId(2L, Pageable.unpaged())} returns PageImpl(listOf(controllerInstitutionList))
        assertThat(getController.getControllers(Pageable.unpaged()).content).containsExactly(controllerInstitutionList)
    }

    @Test
    fun getControllerInstitutionById() {
        every { controllerInstitutionAuthorization.hasPermission(UserRolePermission.InstitutionsUnlimited)} returns true
        every {controllerInstitutionPersistence.getControllerInstitutionById(INSTITUTION_ID)} returns controllerInstitution
        assertThat(getController.getControllerInstitutionById(INSTITUTION_ID)).isEqualTo(controllerInstitution)
    }
}
