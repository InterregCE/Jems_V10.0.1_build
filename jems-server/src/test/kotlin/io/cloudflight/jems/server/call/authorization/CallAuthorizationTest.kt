package io.cloudflight.jems.server.call.authorization

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CallAuthorizationTest {

    @MockK
    lateinit var securityService: SecurityService
    @MockK
    lateinit var callPersistence: CallPersistence

    lateinit var callAuthorization: CallAuthorization

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        callAuthorization =
            CallAuthorization(securityService, callPersistence)
    }

    @Test
    fun `user with retrieve permission can read calls`() {
        val currentUser = LocalCurrentUser(
            AuthorizationUtil.userApplicant, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.CallRetrieve.key))
        )
        every { securityService.currentUser } returns currentUser

        assertTrue(
            callAuthorization.canRetrieveCall(1L),
            "${currentUser.user.email} should be able to retrieve calls"
        )
    }

    @Test
    fun `a published call is readable by default`() {
        every { securityService.currentUser } returns AuthorizationUtil.applicantUser
        every { callPersistence.getCallById(1L) } returns CallDetail(
            id = 1,
            name = "published call",
            status = CallStatus.PUBLISHED,
            startDate = ZonedDateTime.now().minusDays(2),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(2),
            isAdditionalFundAllowed = false,
            lengthOfPeriod = 10,
            applicationFormFieldConfigurations = mutableSetOf()
        )

        assertTrue(
            callAuthorization.canRetrieveCall(1L), "published call is readable"
        )
    }

}
