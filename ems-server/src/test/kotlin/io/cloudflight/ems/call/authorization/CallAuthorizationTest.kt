package io.cloudflight.ems.call.authorization

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.call.service.CallService
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.programmeUser
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
    lateinit var callService: CallService

    lateinit var callAuthorization: CallAuthorization

    private fun dummyCallWithStatus(status: CallStatus) = OutputCall(
        id = 1,
        name = "test call",
        priorityPolicies = emptyList(),
        status = status,
        startDate = ZonedDateTime.now().minusDays(2),
        endDate = ZonedDateTime.now().plusDays(2),
        lengthOfPeriod = 1
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        callAuthorization =
            CallAuthorization(securityService, callService)
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `this user can create a call`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        assertTrue(
            callAuthorization.canCreateCall(),
            "${currentUser.user.email} should be able to create call"
        )
    }

    @Test
    fun `applicant user cannot create call`() {
        every { securityService.currentUser } returns applicantUser

        assertFalse(
            callAuthorization.canCreateCall(),
            "${applicantUser.user.email} should not be able to create call"
        )
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `this user can update call`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        var status = CallStatus.PUBLISHED
        every { callService.getCallById(eq(1)) } returns dummyCallWithStatus(status)
        assertFalse(
            callAuthorization.canUpdateCall(1L),
            "${currentUser.user.email} should NOT be able to update call which is in status $status"
        )

        status = CallStatus.DRAFT
        every { callService.getCallById(eq(2)) } returns dummyCallWithStatus(status)
        assertTrue(
            callAuthorization.canUpdateCall(2L),
            "${currentUser.user.email} should be able to update call which is in status $status"
        )
    }

    @Test
    fun `applicant user cannot update any call`() {
        every { securityService.currentUser } returns applicantUser
        every { callService.getCallById(1L) } returns dummyCallWithStatus(CallStatus.PUBLISHED)
        every { callService.getCallById(2L) } returns dummyCallWithStatus(CallStatus.DRAFT)
        every { callService.getCallById(-1L) } throws ResourceNotFoundException("call")

        var exception = assertThrows<ResourceNotFoundException>(
            "${applicantUser.user.email} should not be able to update existing ${CallStatus.PUBLISHED} call"
        ) { callAuthorization.canUpdateCall(1L) }
        assertThat(exception.entity).isEqualTo("call")

        exception = assertThrows<ResourceNotFoundException>(
            "${applicantUser.user.email} should not be able to update existing ${CallStatus.DRAFT} call"
        ) { callAuthorization.canUpdateCall(2L) }
        assertThat(exception.entity).isEqualTo("call")

        exception = assertThrows<ResourceNotFoundException>(
            "${applicantUser.user.email} should not be able to update non-existing call"
        ) { callAuthorization.canUpdateCall(-1L) }
        assertThat(exception.entity).isEqualTo("call")
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `this user can read call detail anytime`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser

        listOf(CallStatus.PUBLISHED, CallStatus.DRAFT).forEach {
            every { callService.getCallById(1L) } returns dummyCallWithStatus(it)
            assertTrue(
                callAuthorization.canReadCallDetail(1L),
                "${currentUser.user.email} should be able to read call detail (call status was $it)"
            )
        }
    }

    @Test
    fun `applicant user cannot read call based on conditions`() {
        every { securityService.currentUser } returns applicantUser

        val ID_PUBLISHED = 1L
        val ID_DRAFT = 2L
        val ID_PUBLISHED_BUT_CLOSED = 3L
        every { callService.getCallById(ID_PUBLISHED) } returns dummyCallWithStatus(CallStatus.PUBLISHED)
        every { callService.getCallById(ID_DRAFT) } returns dummyCallWithStatus(CallStatus.DRAFT)
        every { callService.getCallById(ID_PUBLISHED_BUT_CLOSED) } returns
            dummyCallWithStatus(CallStatus.PUBLISHED).copy(endDate = ZonedDateTime.now().minusDays(1))

        assertTrue(
            callAuthorization.canReadCallDetail(ID_PUBLISHED),
            "${applicantUser.user.email} should be able to read call detail which is in status ${CallStatus.PUBLISHED})"
        )

        val exception = assertThrows<ResourceNotFoundException>(
            "${applicantUser.user.email} should NOT be able to find call detail which is in status ${CallStatus.DRAFT})"
        ) { callAuthorization.canReadCallDetail(ID_DRAFT) }
        assertThat(exception.entity).isEqualTo("call")

        assertTrue(
            callAuthorization.canReadCallDetail(ID_PUBLISHED_BUT_CLOSED),
            "${applicantUser.user.email} should be able to read EXPIRED call detail which is in status ${CallStatus.PUBLISHED})"
        )
    }

    private fun provideAdminAndProgramUsers(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(programmeUser),
            Arguments.of(adminUser)
        )
    }

}
