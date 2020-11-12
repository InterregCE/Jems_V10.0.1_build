package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.InputCallCreate
import io.cloudflight.jems.api.call.dto.InputCallUpdate
import io.cloudflight.jems.api.call.dto.OutputCall
import io.cloudflight.jems.api.call.dto.OutputCallList
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.flatrate.InputCallFlatRateSetup
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.DigitalConnectivity
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.jems.server.user.repository.UserRepository
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.security.service.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.security.service.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.security.service.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.testUser
import io.cloudflight.jems.server.programme.entity.ProgrammeFund
import io.cloudflight.jems.server.programme.repository.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.entity.Strategy
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime
import java.util.Optional
import java.util.stream.Collectors
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CallServiceTest {

    private val call = callWithId(id = 0)

    private fun outputCallWithId(id: Long) = OutputCall(
        id = id,
        name = call.name,
        priorityPolicies = emptyList(),
        strategies = emptyList(),
        funds = emptyList(),
        startDate = call.startDate,
        endDate = call.endDate,
        status = call.status,
        lengthOfPeriod = call.lengthOfPeriod,
        description = call.description,
        flatRates = listOf(
            InputCallFlatRateSetup(type = FlatRateType.StaffCost, rate = 5, isAdjustable = true)
        )
    )

    private fun outputCallListWithId(id: Long) = OutputCallList(
        id = id,
        name = call.name,
        startDate = call.startDate,
        endDate = call.endDate,
        status = call.status
    )

    @MockK
    lateinit var callRepository: CallRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository

    @MockK
    lateinit var strategyRepository: StrategyRepository

    @MockK
    lateinit var fundRepository: ProgrammeFundRepository

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var callService: CallService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(testUser, "hash_pass", emptyList())
        callService = CallServiceImpl(
            callRepository,
            userRepository,
            programmePriorityPolicyRepository,
            strategyRepository,
            fundRepository,
            auditService,
            securityService
        )
    }

    @Test
    fun getCallById() {
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithId(1))
        assertThat(callService.getCallById(1)).isEqualTo(outputCallWithId(1))
    }

    @Test
    fun `getCallById not existing`() {
        every { callRepository.findById(eq(1)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { callService.getCallById(1) }
    }

    @ParameterizedTest
    @MethodSource("provideAdminAndProgramUsers")
    fun `getAllCalls programme or admin user`(currentUser: LocalCurrentUser) {
        every { securityService.currentUser } returns currentUser
        every { callRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(callWithId(1)))

        val expectedResult = listOf(outputCallListWithId(1))
        val result = callService.getCalls(Pageable.unpaged()).get().collect(Collectors.toList())
        assertThat(result).isEqualTo(expectedResult)
    }

    private fun provideAdminAndProgramUsers(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(programmeUser),
            Arguments.of(adminUser)
        )
    }

    @Test
    fun `getAllCalls applicantUser`() {
        every { securityService.currentUser } returns applicantUser
        every { callRepository.findAllByStatus(eq(CallStatus.PUBLISHED), any<Pageable>()) } returns
                PageImpl(listOf(callWithId(1)))

        val expectedResult = listOf(outputCallListWithId(1))

        val result = callService.getCalls(Pageable.unpaged()).get().collect(Collectors.toList())
        assertThat(result).isEqualTo(expectedResult)
    }


    @Test
    fun `createCall Successful empty policies`() {
        every { securityService.currentUser } returns adminUser
        every { userRepository.findById(eq(adminUser.user.id!!)) } returns Optional.of(call.creator)
        every { callRepository.save(any<Call>()) } returns Call(
            100,
            call.creator,
            call.name,
            call.priorityPolicies,
            call.strategies,
            call.funds,
            call.startDate,
            call.endDate.withSecond(59).withNano(999999999),
            CallStatus.DRAFT,
            call.lengthOfPeriod,
            call.description

        )

        val newCall = InputCallCreate(
            name = call.name,
            priorityPolicies = null,
            startDate = call.startDate,
            endDate = call.endDate,
            description = call.description,
            lengthOfPeriod = 12
        )
        val endDate = call.endDate.withSecond(59).withNano(999999999)

        val result = callService.createCall(newCall)
        assertThat(result.name).isEqualTo(call.name)
        assertThat(result.startDate).isEqualTo(call.startDate)
        assertThat(result.endDate).isEqualTo(endDate)
        assertThat(result.status).isEqualTo(CallStatus.DRAFT)
        assertThat(result.description).isEqualTo(call.description)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(AuditAction.CALL_CREATED).isEqualTo(captured.action)
            assertThat("A new call id=100 'Test call name' was created").isEqualTo(captured.description)
        }
    }

    @Test
    fun `createCall Successful with policies`() {
        every { securityService.currentUser } returns adminUser
        every { userRepository.findById(eq(adminUser.user.id!!)) } returns Optional.of(call.creator)
        every { callRepository.save(any<Call>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findAllById(eq(setOf(AdvancedTechnologies))) } returns
                listOf(ProgrammePriorityPolicy(programmeObjectivePolicy = AdvancedTechnologies, code = "AT"))
        every { strategyRepository.findAllById(eq(setOf(ProgrammeStrategy.EUStrategyBalticSeaRegion))) } returns
                listOf(Strategy(ProgrammeStrategy.EUStrategyBalticSeaRegion, true))
        every { fundRepository.findAllById(eq(setOf(1L))) } returns
                listOf(ProgrammeFund(1, "test", "test description", true))

        val newCall = InputCallCreate(
            name = call.name,
            priorityPolicies = setOf(AdvancedTechnologies),
            strategies = setOf(ProgrammeStrategy.EUStrategyBalticSeaRegion),
            funds = setOf(1L),
            startDate = call.startDate,
            endDate = call.endDate,
            lengthOfPeriod = 12
        )

        val result = callService.createCall(newCall)
        assertThat(result.name).isEqualTo(call.name)
        assertThat(result.priorityPolicies).isEqualTo(
            listOf(
                OutputProgrammePriorityPolicySimple(
                    AdvancedTechnologies,
                    "AT"
                )
            )
        )
        assertThat(result.strategies).isEqualTo(listOf(ProgrammeStrategy.EUStrategyBalticSeaRegion))
        assertThat(result.status).isEqualTo(CallStatus.DRAFT)
    }

    @Test
    fun `createCall Unsuccessful with not existing policies`() {
        every { securityService.currentUser } returns adminUser
        every { userRepository.findById(eq(adminUser.user.id!!)) } returns Optional.of(call.creator)
        every { callRepository.save(any<Call>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findAllById(eq(setOf(DigitalConnectivity))) } returns emptyList()

        val newCall = InputCallCreate(
            name = call.name,
            priorityPolicies = setOf(DigitalConnectivity),
            startDate = call.startDate,
            endDate = call.endDate,
            lengthOfPeriod = 12
        )

        val exception = assertThrows<ResourceNotFoundException> { callService.createCall(newCall) }
        assertThat(exception.entity).isEqualTo("programme_priority_policy")
    }

    @Test
    fun `createCall no user`() {
        every { userRepository.findById(eq(testUser.id!!)) } returns Optional.empty()

        val newCall = InputCallCreate(
            name = call.name,
            priorityPolicies = null,
            startDate = call.startDate,
            endDate = call.endDate,
            description = call.description,
            lengthOfPeriod = 12
        )

        assertThrows<ResourceNotFoundException> { callService.createCall(newCall) }
    }

    @Test
    fun `updateCall Successful with name-change`() {
        val existingId = 1L
        val startDate = ZonedDateTime.now().minusDays(2)
        val endDate = ZonedDateTime.now().plusDays(5)
        every { callRepository.findById(eq(existingId)) } returns Optional.of(callWithId(existingId))
        every { callRepository.save(any<Call>()) } returnsArgument 0

        val programmePriorityPolicy =
            ProgrammePriorityPolicy(programmeObjectivePolicy = AdvancedTechnologies, code = "AT")
        every { programmePriorityPolicyRepository.findById(eq(AdvancedTechnologies)) } returns Optional.of(
            programmePriorityPolicy
        )

        val newDataForCall = InputCallUpdate(
            id = existingId,
            name = "new name",
            priorityPolicies = setOf(AdvancedTechnologies),
            startDate = startDate,
            endDate = endDate,
            description = "new description",
            lengthOfPeriod = 12
        )
        every { callRepository.findOneByName("new name") } returns null

        val result = callService.updateCall(newDataForCall)
        assertThat(result.name).isEqualTo("new name")
        assertThat(result.priorityPolicies).isEqualTo(
            listOf(
                OutputProgrammePriorityPolicySimple(
                    programmeObjectivePolicy = AdvancedTechnologies,
                    code = "AT"
                )
            )
        )
        assertThat(result.startDate).isEqualTo(startDate)
        assertThat(result.endDate).isEqualTo(endDate)
        assertThat(result.description).isEqualTo("new description")
    }

    @Test
    fun `updateCall not unique name`() {
        val ID_10 = 10L
        val ID_35 = 35L
        val startDate = ZonedDateTime.now().minusDays(2)
        val endDate = ZonedDateTime.now().plusDays(5)
        every { callRepository.findById(eq(ID_10)) } returns Optional.of(callWithId(ID_10))
        every { callRepository.save(any<Call>()) } returnsArgument 0

        val newDataForCall = InputCallUpdate(
            id = ID_10,
            name = "existing name",
            priorityPolicies = emptySet(),
            startDate = startDate,
            endDate = endDate,
            description = "new description",
            lengthOfPeriod = 12
        )
        every { callRepository.findOneByName("existing name") } returns callWithId(ID_35)

        val expectedException = I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("name" to I18nFieldError("call.name.already.in.use"))
        )
        val exception = assertThrows<I18nValidationException> { callService.updateCall(newDataForCall) }
        assertThat(exception).isEqualTo(expectedException)
    }

    @Test
    fun `updateCall Successful without name-change`() {
        val existingId = 1L
        val startDate = ZonedDateTime.now().minusDays(2)
        val endDate = ZonedDateTime.now().plusDays(5)
        every { callRepository.findById(eq(existingId)) } returns Optional.of(callWithId(existingId))
        every { callRepository.save(any<Call>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findAllById(eq(emptySet())) } returns emptyList()

        val newDataForCall = InputCallUpdate(
            id = existingId,
            name = call.name, // no change
            priorityPolicies = emptySet(),
            startDate = startDate,
            endDate = endDate,
            description = "new description",
            lengthOfPeriod = 12
        )
        every { callRepository.findOneByName(eq(call.name)) } returns callWithId(existingId)

        val result = callService.updateCall(newDataForCall)
        assertThat(result.name).isEqualTo(call.name)
        assertThat(result.startDate).isEqualTo(startDate)
        assertThat(result.endDate).isEqualTo(endDate)
        assertThat(result.description).isEqualTo("new description")
    }

    @Test
    fun `updateCall not existing`() {
        val notExistingId = -1L
        every { callRepository.findById(eq(notExistingId)) } returns Optional.empty()

        val newCall = InputCallUpdate(
            id = notExistingId,
            name = call.name,
            priorityPolicies = emptySet(),
            startDate = call.startDate,
            endDate = call.endDate,
            description = call.description,
            lengthOfPeriod = 12
        )

        assertThrows<ResourceNotFoundException> { callService.updateCall(newCall) }
    }

    @Test
    fun `publishCall not existing`() {
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { callService.publishCall(-1) }
        assertThat(exception.entity).isEqualTo("call")
    }

    @Test
    fun `publishCall Successful`() {
        val existingId = 1L
        val policies = setOf(ProgrammePriorityPolicy(programmeObjectivePolicy = AdvancedTechnologies, code = "AT"))
        val funds =
            setOf(ProgrammeFund(id = 1, abbreviation = "test", description = "test description", selected = true))

        every { callRepository.findById(eq(existingId)) } returns
                Optional.of(
                    callWithId(existingId).copy(
                        status = CallStatus.DRAFT,
                        priorityPolicies = policies,
                        funds = funds
                    )
                )
        every { callRepository.save(any<Call>()) } returnsArgument 0

        val result = callService.publishCall(existingId)
        assertThat(result.status).isEqualTo(CallStatus.PUBLISHED)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(captured.action).isEqualTo(AuditAction.CALL_PUBLISHED)
            assertThat(captured.description)
                .isEqualTo("Call id=1 'Test call name' published")
        }
    }

    @Test
    fun `publishCall already published`() {
        val existingId = 1L
        every { callRepository.findById(eq(existingId)) } returns
                Optional.of(callWithId(existingId).copy(status = CallStatus.PUBLISHED))
        every { callRepository.save(any<Call>()) } returnsArgument 0

        val expectedException = I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "call.state.cannot.publish"
        )
        val exception = assertThrows<I18nValidationException> { callService.publishCall(existingId) }
        assertThat(exception).isEqualTo(expectedException)
    }

    @Test
    fun `publishCall empty policies`() {
        val existingId = 1L
        val policies = emptySet<ProgrammePriorityPolicy>()
        every { callRepository.findById(eq(existingId)) } returns
                Optional.of(callWithId(existingId).copy(status = CallStatus.DRAFT, priorityPolicies = policies))
        every { callRepository.save(any<Call>()) } returnsArgument 0

        val expectedException = I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "call.priorityPolicies.is.empty"
        )
        val exception = assertThrows<I18nValidationException> { callService.publishCall(existingId) }
        assertThat(exception).isEqualTo(expectedException)
    }

    @Test
    fun `findOneByName existing`() {
        every { callRepository.findOneByName(eq("existing")) } returns callWithId(10L)
        assertThat(callService.findOneByName("existing")).isEqualTo(outputCallWithId(10L))
    }

    @Test
    fun `findOneByName non-existing`() {
        every { callRepository.findOneByName(eq("non-existing")) } returns null
        assertThat(callService.findOneByName("non-existing")).isNull()
    }

}
