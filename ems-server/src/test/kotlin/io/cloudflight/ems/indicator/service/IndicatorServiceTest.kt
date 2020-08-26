package io.cloudflight.ems.indicator.service

import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputUpdate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultUpdate
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorOutput
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorResult
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.indicator.entity.IndicatorOutput
import io.cloudflight.ems.indicator.entity.IndicatorResult
import io.cloudflight.ems.indicator.repository.IndicatorOutputRepository
import io.cloudflight.ems.indicator.repository.IndicatorResultRepository
import io.cloudflight.ems.programme.entity.ProgrammePriority
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.ems.service.AuditService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.Optional

class IndicatorServiceTest {

    companion object {
        private val priority = ProgrammePriority(
            id = 1,
            code = "prio_01",
            title = "prio_01 title",
            objective = ProgrammeObjective.PO2,
            programmePriorityPolicies = emptySet() // not used here
        )

        private val priorityPolicy = ProgrammePriorityPolicy(
            programmeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
            code = "RE",
            programmePriority = priority
        )

        private val testIndicatorOutput = IndicatorOutput(
            id = 1L,
            identifier = "ID01",
            code = "ioCODE",
            name = "indicator title",
            programmePriorityPolicy = priorityPolicy,
            measurementUnit = "measurement unit",
            milestone = BigDecimal.TEN,
            finalTarget = BigDecimal.ONE
        )

        private val testOutputIndicatorOutput = OutputIndicatorOutput(
            id = 1L,
            identifier = testIndicatorOutput.identifier,
            code = testIndicatorOutput.code,
            name = testIndicatorOutput.name,
            programmePriorityPolicySpecificObjective = priorityPolicy.programmeObjectivePolicy,
            programmePriorityPolicyCode = priorityPolicy.code,
            programmePriorityCode = priorityPolicy.programmePriority!!.code,
            measurementUnit = testIndicatorOutput.measurementUnit,
            milestone = testIndicatorOutput.milestone,
            finalTarget = testIndicatorOutput.finalTarget
        )

        private val testIndicatorResult = IndicatorResult(
            id = 1L,
            identifier = "ID10",
            code = "ioCODE",
            name = "indicator title",
            programmePriorityPolicy = priorityPolicy,
            measurementUnit = "measurement unit",
            baseline = BigDecimal.TEN,
            referenceYear = "2022/2023",
            finalTarget = BigDecimal.ONE,
            sourceOfData = "test source of data",
            comment = "test comment"
        )

        private val testOutputIndicatorResult = OutputIndicatorResult(
            id = 1L,
            identifier = testIndicatorResult.identifier,
            code = testIndicatorResult.code,
            name = testIndicatorResult.name,
            programmePriorityPolicySpecificObjective = priorityPolicy.programmeObjectivePolicy,
            programmePriorityPolicyCode = priorityPolicy.code,
            programmePriorityCode = priorityPolicy.programmePriority!!.code,
            measurementUnit = testIndicatorResult.measurementUnit,
            baseline = testIndicatorResult.baseline,
            referenceYear = testIndicatorResult.referenceYear,
            finalTarget = testIndicatorResult.finalTarget,
            sourceOfData = testIndicatorResult.sourceOfData,
            comment = testIndicatorResult.comment
        )
    }

    @MockK
    lateinit var indicatorResultRepository: IndicatorResultRepository
    @MockK
    lateinit var indicatorOutputRepository: IndicatorOutputRepository
    @MockK
    lateinit var programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository
    @MockK
    lateinit var securityService: SecurityService
    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var indicatorService: IndicatorService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns adminUser
        indicatorService = IndicatorServiceImpl(
            indicatorResultRepository,
            indicatorOutputRepository,
            programmePriorityPolicyRepository,
            auditService,
            securityService
        )
    }

    //region INDICATOR OUTPUT

    @Test
    fun `getOutputIndicatorById not found`() {
        every { indicatorOutputRepository.findById(eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { indicatorService.getOutputIndicatorById(-1) }
        assertThat(exception.entity).isEqualTo("indicator_output")
    }

    @Test
    fun getOutputIndicatorById() {
        every { indicatorOutputRepository.findById(eq(1)) } returns Optional.of(testIndicatorOutput)
        assertThat(indicatorService.getOutputIndicatorById(1))
            .isEqualTo(testOutputIndicatorOutput)
    }

    @Test
    fun getOutputIndicators() {
        every { indicatorOutputRepository.findAll(eq(Pageable.unpaged())) } returns PageImpl(listOf(testIndicatorOutput))
        assertThat(indicatorService.getOutputIndicators(Pageable.unpaged()))
            .isEqualTo(PageImpl(listOf(testOutputIndicatorOutput)))
    }

    @Test
    fun existsOutputByIdentifier() {
        every { indicatorOutputRepository.existsByIdentifier(eq("existing identifier")) } returns true
        assertTrue(indicatorService.existsOutputByIdentifier("existing identifier"))
    }

    @Test
    fun `save create indicatorOutput`() {
        every { indicatorOutputRepository.save(any<IndicatorOutput>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns Optional.of(priorityPolicy)

        val indicatorCreate = InputIndicatorOutputCreate(
            identifier = testIndicatorOutput.identifier,
            code = testIndicatorOutput.code,
            name = testIndicatorOutput.name,
            programmeObjectivePolicy = priorityPolicy.programmeObjectivePolicy,
            measurementUnit = testIndicatorOutput.measurementUnit,
            milestone = testIndicatorOutput.milestone,
            finalTarget = testIndicatorOutput.finalTarget
        )
        assertThat(indicatorService.save(indicatorCreate))
            .isEqualTo(testOutputIndicatorOutput.copy(id = null)) // not a real repository

        val auditLog = slot<Audit>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_ADDED)
            assertThat(description).isEqualTo("Programme indicator ID01 has been added")
        }
    }

    @Test
    fun `save update indicatorOutput`() {
        every { indicatorOutputRepository.findById(eq(10)) } returns Optional.of(testIndicatorOutput)
        every { indicatorOutputRepository.save(any<IndicatorOutput>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns Optional.of(priorityPolicy)

        val indicatorUpdate = InputIndicatorOutputUpdate(
            id = 10,
            identifier = testIndicatorOutput.identifier,
            code = testIndicatorOutput.code,
            name = testIndicatorOutput.name,
            programmeObjectivePolicy = priorityPolicy.programmeObjectivePolicy,
            measurementUnit = "new measurement unit",
            milestone = testIndicatorOutput.milestone,
            finalTarget = testIndicatorOutput.finalTarget
        )
        assertThat(indicatorService.save(indicatorUpdate))
            .isEqualTo(testOutputIndicatorOutput.copy(id = 10, measurementUnit = "new measurement unit")) // not a real repository

        val auditLog = slot<Audit>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description).isEqualTo("Programme indicator ID01 edited:\nmeasurementUnit changed from measurement unit to new measurement unit")
        }
    }
    //endregion

    //region INDICATOR RESULT

    @Test
    fun `getResultIndicatorById not found`() {
        every { indicatorResultRepository.findById(eq(-1)) } returns Optional.empty()
        val exception = assertThrows<ResourceNotFoundException> { indicatorService.getResultIndicatorById(-1) }
        assertThat(exception.entity).isEqualTo("indicator_result")
    }

    @Test
    fun getResultIndicatorById() {
        every { indicatorResultRepository.findById(eq(1)) } returns Optional.of(testIndicatorResult)
        assertThat(indicatorService.getResultIndicatorById(1))
            .isEqualTo(testOutputIndicatorResult)
    }

    @Test
    fun getResultIndicators() {
        every { indicatorResultRepository.findAll(eq(Pageable.unpaged())) } returns PageImpl(listOf(testIndicatorResult))
        assertThat(indicatorService.getResultIndicators(Pageable.unpaged()))
            .isEqualTo(PageImpl(listOf(testOutputIndicatorResult)))
    }

    @Test
    fun existsResultByIdentifier() {
        every { indicatorResultRepository.existsByIdentifier(eq("existing identifier")) } returns true
        assertTrue(indicatorService.existsResultByIdentifier("existing identifier"))
    }

    @Test
    fun `save create indicatorResult`() {
        every { indicatorResultRepository.save(any<IndicatorResult>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns Optional.of(priorityPolicy)

        val indicatorCreate = InputIndicatorResultCreate(
            identifier = testIndicatorResult.identifier,
            code = testIndicatorResult.code,
            name = testIndicatorResult.name,
            programmeObjectivePolicy = priorityPolicy.programmeObjectivePolicy,
            measurementUnit = testIndicatorResult.measurementUnit,
            baseline = testIndicatorResult.baseline,
            referenceYear = testIndicatorResult.referenceYear,
            finalTarget = testIndicatorResult.finalTarget,
            sourceOfData = testIndicatorResult.sourceOfData,
            comment = testIndicatorResult.comment
        )
        assertThat(indicatorService.save(indicatorCreate))
            .isEqualTo(testOutputIndicatorResult.copy(id = null)) // not a real repository

        val auditLog = slot<Audit>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_ADDED)
            assertThat(description).isEqualTo("Programme indicator ID10 has been added")
        }
    }

    @Test
    fun `save update indicatorResult`() {
        every { indicatorResultRepository.findById(eq(10)) } returns Optional.of(testIndicatorResult)
        every { indicatorResultRepository.save(any<IndicatorResult>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns Optional.of(priorityPolicy)

        val indicatorUpdate = InputIndicatorResultUpdate(
            id = 10,
            identifier = testIndicatorResult.identifier,
            code = testIndicatorResult.code,
            name = testIndicatorResult.name,
            programmeObjectivePolicy = priorityPolicy.programmeObjectivePolicy,
            measurementUnit = "new measurement unit",
            baseline = testIndicatorResult.baseline,
            referenceYear = testIndicatorResult.referenceYear,
            finalTarget = testIndicatorResult.finalTarget,
            sourceOfData = testIndicatorResult.sourceOfData,
            comment = testIndicatorResult.comment
        )
        assertThat(indicatorService.save(indicatorUpdate))
            .isEqualTo(testOutputIndicatorResult.copy(id = 10, measurementUnit = "new measurement unit")) // not a real repository

        val auditLog = slot<Audit>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description).isEqualTo("Programme indicator ID10 edited:\nmeasurementUnit changed from measurement unit to new measurement unit")
        }
    }

    //endregion

}
