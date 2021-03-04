package io.cloudflight.jems.server.call.authorization

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.repository.CallNotFound
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.ZonedDateTime
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CallAuthorizationTest {

    @MockK
    lateinit var securityService: SecurityService
    @MockK
    lateinit var callPersistence: CallPersistence

    lateinit var callAuthorization: CallAuthorization

    private fun dummyCallWithStatus(status: CallStatus) = CallDetail(
        id = 1,
        name = "test call",
        status = status,
        startDate = ZonedDateTime.now().minusDays(2),
        endDate = ZonedDateTime.now().plusDays(2),
        isAdditionalFundAllowed = false,
        lengthOfPeriod = 10,
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        callAuthorization =
            CallAuthorization(securityService, callPersistence)
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `this user can create or update calls`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        assertTrue(
            callAuthorization.canUpdateCalls(),
            "${currentUser.user.email} should be able to create or update calls"
        )
    }

    @Test
    fun `applicant user cannot update any call`() {
        every { securityService.currentUser } returns applicantUser
        assertThat(callAuthorization.canUpdateCalls()).isFalse
            .overridingErrorMessage("applicant can never update call")
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `this user can read call detail anytime`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(CallStatus.PUBLISHED, CallStatus.DRAFT).forEach {
            every { callPersistence.getCallById(1L) } returns dummyCallWithStatus(it)
            assertTrue(
                callAuthorization.canReadCall(1L),
                "${currentUser.user.email} should be able to read call detail (call status was $it)"
            )
        }
    }

    @Test
    fun `applicant user can-cannot read call`() {
        every { securityService.currentUser } returns applicantUser

        val ID_EXISTING_DRAFT = 1L
        val ID_EXISTING_PUBLISHED = 2L
        val ID_NOT_EXISTING = 3L
        every { callPersistence.getCallById(ID_EXISTING_DRAFT) } returns dummyCallWithStatus(CallStatus.DRAFT)
        every { callPersistence.getCallById(ID_EXISTING_PUBLISHED) } returns dummyCallWithStatus(CallStatus.PUBLISHED)
        every { callPersistence.getCallById(ID_NOT_EXISTING) } throws CallNotFound()

        assertTrue(
            callAuthorization.canReadCall(ID_EXISTING_PUBLISHED),
            "${applicantUser.user.email} should be able to read call detail which is in status ${CallStatus.PUBLISHED})"
        )

        assertFalse(
            callAuthorization.canReadCall(ID_EXISTING_DRAFT),
            "${applicantUser.user.email} should get FORBIDDEN when status ${CallStatus.DRAFT}"
        )

        assertFalse(
            callAuthorization.canReadCall(ID_NOT_EXISTING),
            "${applicantUser.user.email} should get FORBIDDEN when call does not exist"
        )
    }

    private fun provideAdminAndProgramUsers(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(programmeUser),
            Arguments.of(adminUser)
        )
    }

}
