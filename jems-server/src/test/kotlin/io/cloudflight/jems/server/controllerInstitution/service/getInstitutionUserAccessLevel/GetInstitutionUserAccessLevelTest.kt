package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUserAccessLevel

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetInstitutionUserAccessLevelTest: UnitTest() {

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var securityService: SecurityService


    @InjectMockKs
    lateinit var getInstitutionUserAccessLevel: GetInstitutionUserAccessLevel


    @Test
    fun getControllerUserAccessLevelForPartner() {
        val accessLevel = mockk<UserInstitutionAccessLevel>()
        every { controllerInstitutionPersistence.getControllerUserAccessLevelForPartner(
            userId = 20L,
            partnerId = 497L,
        ) } returns accessLevel
        every { securityService.getUserIdOrThrow() } returns 20L
        Assertions.assertThat(getInstitutionUserAccessLevel.getControllerUserAccessLevelForPartner(497L))
            .isEqualTo(accessLevel)
    }
}
