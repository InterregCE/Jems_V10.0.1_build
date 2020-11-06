package io.cloudflight.jems.server.indicator.service

import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputUpdate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultUpdate
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorOutput
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorResult
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorResultRepository
import io.cloudflight.jems.server.programme.entity.ProgrammePriority
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.jems.server.programme.service.indicator.IndicatorService
import io.cloudflight.jems.server.programme.service.indicator.IndicatorServiceImpl
import io.cloudflight.jems.server.programme.service.indicator.toEntity
import io.cloudflight.jems.server.programme.service.indicator.toOutputIndicator
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
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.util.Optional.empty
import java.util.Optional.of

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

        private val testOutputIndicatorResult = testIndicatorResult.toOutputIndicator()
    }

    @MockK
    lateinit var indicatorResultRepository: IndicatorResultRepository
    @MockK
    lateinit var indicatorOutputRepository: IndicatorOutputRepository
    @MockK
    lateinit var programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository
    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var indicatorService: IndicatorService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        indicatorService = IndicatorServiceImpl(
            indicatorResultRepository,
            indicatorOutputRepository,
            programmePriorityPolicyRepository,
            auditService
        )
    }

    //region INDICATOR OUTPUT

    @Test
    fun `getOutputIndicatorById not found`() {
        every { indicatorOutputRepository.findById(eq(-1)) } returns empty()
        val exception = assertThrows<ResourceNotFoundException> { indicatorService.getOutputIndicatorById(-1) }
        assertThat(exception.entity).isEqualTo("indicator_output")
    }

    @Test
    fun getOutputIndicatorById() {
        every { indicatorOutputRepository.findById(eq(1)) } returns of(testIndicatorOutput)
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
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns of(priorityPolicy)

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
            .isEqualTo(testOutputIndicatorOutput.copy(id = 0)) // not a real repository

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_ADDED)
            assertThat(description).isEqualTo("Programme indicator ID01 has been added")
        }
    }

    @Test
    fun `save update indicatorOutput no change in identifier`() {
        every { indicatorOutputRepository.findById(eq(10)) } returns of(testIndicatorOutput)
        every { indicatorOutputRepository.save(any<IndicatorOutput>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns of(priorityPolicy)
        every { indicatorOutputRepository.findOneByIdentifier(eq(testIndicatorOutput.identifier)) } returns testIndicatorOutput

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

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description).isEqualTo("Programme indicator ID01 edited:\nmeasurementUnit changed from measurement unit to new measurement unit")
        }
    }

    @Test
    fun `save update indicatorOutput change in identifier`() {
        every { indicatorOutputRepository.findById(eq(10)) } returns of(testIndicatorOutput)
        every { indicatorOutputRepository.save(any<IndicatorOutput>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns of(priorityPolicy)
        every { indicatorOutputRepository.findOneByIdentifier(eq("newID")) } returns null

        val indicatorUpdate = InputIndicatorOutputUpdate(
            id = 10,
            identifier = "newID",
            code = testIndicatorOutput.code,
            name = testIndicatorOutput.name,
            programmeObjectivePolicy = priorityPolicy.programmeObjectivePolicy,
            measurementUnit = testIndicatorOutput.measurementUnit,
            milestone = testIndicatorOutput.milestone,
            finalTarget = testIndicatorOutput.finalTarget
        )
        assertThat(indicatorService.save(indicatorUpdate))
            .isEqualTo(testOutputIndicatorOutput.copy(id = 10, identifier = "newID")) // not a real repository

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description).isEqualTo("Programme indicator newID edited:\nidentifier changed from ID01 to newID")
        }
    }

    @Test
    fun `save update indicatorOutput identifier in use`() {
        every { indicatorOutputRepository.findById(eq(10)) } returns of(testIndicatorOutput)
        every { indicatorOutputRepository.save(any<IndicatorOutput>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns of(priorityPolicy)
        every { indicatorOutputRepository.findOneByIdentifier(eq(testIndicatorOutput.identifier)) } returns testIndicatorOutput.copy(id = 30) // different indicator

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
        val exception = assertThrows<I18nValidationException> { indicatorService.save(indicatorUpdate) }
        val expected = I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("identifier" to I18nFieldError("indicator.identifier.already.in.use"))
        )
        assertThat(exception).isEqualTo(expected)
    }
    //endregion

    //region INDICATOR RESULT

    @Test
    fun `getResultIndicatorById not found`() {
        every { indicatorResultRepository.findById(eq(-1)) } returns empty()
        val exception = assertThrows<ResourceNotFoundException> { indicatorService.getResultIndicatorById(-1) }
        assertThat(exception.entity).isEqualTo("indicator_result")
    }

    @Test
    fun getResultIndicatorById() {
        every { indicatorResultRepository.findById(eq(1)) } returns of(testIndicatorResult)
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
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns of(priorityPolicy)

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
            .isEqualTo(testOutputIndicatorResult.copy(id = 0)) // not a real repository

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_ADDED)
            assertThat(description).isEqualTo("Programme indicator ID10 has been added")
        }
    }

    @Test
    fun `save update indicatorResult`() {
        every { indicatorResultRepository.findById(eq(10)) } returns of(testIndicatorResult)
        every { indicatorResultRepository.save(any<IndicatorResult>()) } returnsArgument 0
        every { programmePriorityPolicyRepository.findById(eq(priorityPolicy.programmeObjectivePolicy)) } returns of(priorityPolicy)
        every { indicatorResultRepository.findOneByIdentifier(eq(testIndicatorResult.identifier)) } returns testIndicatorResult

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

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description).isEqualTo("Programme indicator ID10 edited:\nmeasurementUnit changed from measurement unit to new measurement unit")
        }
    }

    @Test
    fun `save update indicatorResult changed data`() {
        val testIndicatorResultUpdate = InputIndicatorResultUpdate(
            id = 10,
            identifier = "ID11",
            code = "ioCODE-update",
            name = "indicator title2",
            programmeObjectivePolicy = priorityPolicy.programmeObjectivePolicy,
            measurementUnit = "new measurement unit",
            baseline = BigDecimal.ONE,
            referenceYear = "2022/2024",
            finalTarget = BigDecimal.TEN,
            sourceOfData = "test source of data update",
            comment = "test comment update"
        )
        val testIndicatorResultNew = testIndicatorResultUpdate.toEntity(testIndicatorResultUpdate.identifier!!, priorityPolicy)
        val testOutputIndicatorResult = testIndicatorResultNew.toOutputIndicator()

        every { indicatorResultRepository.findById(eq(10)) } returns of(testIndicatorResult)
        every { indicatorResultRepository.save(any<IndicatorResult>()) } returns testIndicatorResultNew
        every { programmePriorityPolicyRepository.findById(testIndicatorResultUpdate.programmeObjectivePolicy!!) } returns of(priorityPolicy)
        every { indicatorResultRepository.findOneByIdentifier(testIndicatorResultUpdate.identifier!!) } returns null

        assertThat(indicatorService.save(testIndicatorResultUpdate)).isEqualTo(testOutputIndicatorResult)

        val auditLog = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditLog)) }
        with(auditLog.captured) {
            assertThat(action).isEqualTo(AuditAction.PROGRAMME_INDICATOR_EDITED)
            assertThat(description).isEqualTo("Programme indicator ID11 edited:\n" +
                "identifier changed from ID10 to ID11,\n" +
                "code changed from ioCODE to ioCODE-update,\n" +
                "name changed from indicator title to indicator title2,\n" +
                "measurementUnit changed from measurement unit to new measurement unit,\n" +
                "baseline changed from 10 to 1,\n" +
                "referenceYear changed from 2022/2023 to 2022/2024,\n" +
                "finalTarget changed from 1 to 10,\n" +
                "sourceOfData changed from test source of data to test source of data update,\n" +
                "comment changed from test comment to test comment update")
        }
    }
    //endregion

}
