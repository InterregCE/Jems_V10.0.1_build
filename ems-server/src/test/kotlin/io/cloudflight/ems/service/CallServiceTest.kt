package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.call.CallStatus
import io.cloudflight.ems.api.dto.call.InputCallCreate
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.entity.Call
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.repository.CallRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.call.CallService
import io.cloudflight.ems.service.call.impl.CallServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime
import java.util.*

class CallServiceTest {

    private val account = User(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN"),
        password = "hash_pass"
    )

    private val user = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    private val call = Call(
        1,
        account,
        "Test call name",
        ZonedDateTime.now(),
        ZonedDateTime.now().plusDays(5L),
        CallStatus.DRAFT,
        "This is a dummy call"
    )

    @MockK
    lateinit var callRepository: CallRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var callService: CallService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        every { userRepository.findById(eq(user.id!!)) } returns Optional.of(account)
        every { callRepository.save(any<Call>()) } returns call
        every { callRepository.findById(eq(call.id!!)) } returns Optional.of(call)
        callService = CallServiceImpl(callRepository, userRepository, auditService, securityService)
    }

    @Test
    fun createCall_Successful() {
        val newCall = InputCallCreate(
            name = "Test call name",
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now().plusDays(5L),
            description = "This is a dummy call"
        )

        val result = callService.createCall(newCall)
        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo("Test call name")
        assertThat(result.status).isEqualTo(CallStatus.DRAFT)

        val event = slot<Audit>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(AuditAction.CALL_CREATED).isEqualTo(captured.action)
            Assertions.assertEquals(
                "New call 'Test call name' was created by admin@admin.dev",
                captured.description
            )
        }
    }

    @Test
    fun getCallById() {
        val result = callService.getCallById(1)

        assertThat(result).isNotNull
    }

    @Test
    fun getAllCalls() {
        every { callRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(call))

        val result = callService.getCalls(Pageable.unpaged())

        assertThat(result.totalElements).isEqualTo(1);
    }
}
