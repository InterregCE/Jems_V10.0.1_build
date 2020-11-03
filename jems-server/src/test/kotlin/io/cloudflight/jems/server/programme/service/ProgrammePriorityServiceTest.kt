package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.user.dto.OutputUserRole
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityPolicy
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriority
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO2
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.ClimateChange
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitalization
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.EnergyEfficiency
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Growth
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.IndustrialTransition
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SmartEnergy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SocialInnovation
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.WaterManagement
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammePriority
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityRepository
import io.cloudflight.jems.server.audit.service.AuditService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional
import java.util.stream.Collectors

class ProgrammePriorityServiceTest {

    private val user = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    @MockK
    lateinit var programmePriorityRepository: ProgrammePriorityRepository
    @MockK
    lateinit var programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository
    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var programmePriorityService: ProgrammePriorityService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmePriorityService = ProgrammePriorityServiceImpl(
            programmePriorityRepository,
            programmePriorityPolicyRepository,
            auditService
        )
    }

    @Test
    fun getAll() {
        val toBeReturned = ProgrammePriority(
            id = 10,
            code = "prio_A",
            title = "Priority A - $PO1",
            objective = PO1,
            programmePriorityPolicies = setOf(
                ProgrammePriorityPolicy(programmeObjectivePolicy = Digitalization, code = "Code-Digit"))
        )
        every { programmePriorityRepository.findAll(any<Pageable>()) } returns PageImpl(listOf(toBeReturned))

        val expectedResultList = listOf(
            OutputProgrammePriority(
                id = 10,
                code = "prio_A",
                title = "Priority A - $PO1",
                objective = PO1,
                programmePriorityPolicies = listOf(
                    OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = Digitalization, code = "Code-Digit"))
            )
        )

        val resultList = programmePriorityService.getAll(Pageable.unpaged()).get().collect(Collectors.toList())
        assertThat(resultList)
            .isEqualTo(expectedResultList)
    }

    @Test
    fun create() {
        val toCreate = InputProgrammePriorityCreate(
            code = "new prio",
            title = "New priority",
            objective = PO1,
            programmePriorityPolicies = setOf(
                InputProgrammePriorityPolicy(programmeObjectivePolicy = Growth, code = "growth"),
                InputProgrammePriorityPolicy(programmeObjectivePolicy = IndustrialTransition, code = "indu")
            )
        )

        every { programmePriorityRepository.save(any<ProgrammePriority>()) } returns ProgrammePriority(
            id = 15,
            code = toCreate.code!!,
            title = toCreate.title!!,
            objective = toCreate.objective!!,
            programmePriorityPolicies = setOf(
                ProgrammePriorityPolicy(programmeObjectivePolicy = IndustrialTransition, code = "indu"),
                ProgrammePriorityPolicy(programmeObjectivePolicy = Growth, code = "growth")
            )
        )

        val expectedResult = OutputProgrammePriority(
            id = 15,
            code = "new prio",
            title = "New priority",
            objective = PO1,
            programmePriorityPolicies = listOf(
                OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = Growth, code = "growth"),
                OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = IndustrialTransition, code = "indu")
            )
        )

        assertThat(programmePriorityService.create(toCreate))
            .isEqualTo(expectedResult)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(AuditAction.PROGRAMME_PRIORITY_ADDED).isEqualTo(captured.action)
            assertThat("New programme priority 'new prio' 'New priority' was created").isEqualTo(captured.description)
        }
    }

    @Test
    fun `create wrong PO policies`() {
        val toCreate = InputProgrammePriorityCreate(
            code = "new prio",
            title = "New priority",
            objective = PO2,
            programmePriorityPolicies = setOf(
                InputProgrammePriorityPolicy(programmeObjectivePolicy = WaterManagement, code = "water"),
                InputProgrammePriorityPolicy(programmeObjectivePolicy = SocialInnovation, code = "social")
            )
        )

        val exception = assertThrows<I18nValidationException> { programmePriorityService.create(toCreate) }

        val expectedException = I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "programme.priority.priorityPolicies.should.not.be.of.different.objectives"
        )
        assertThat(exception).isEqualTo(expectedException)
    }

    @Test
    fun update() {
        val toUpdate = InputProgrammePriorityUpdate(
            id = 20,
            code = "existing prio",
            title = "Existing priority",
            objective = PO2,
            programmePriorityPolicies = setOf(
                InputProgrammePriorityPolicy(programmeObjectivePolicy = WaterManagement, code = "water"),
                InputProgrammePriorityPolicy(programmeObjectivePolicy = SmartEnergy, code = "smart"),
                InputProgrammePriorityPolicy(programmeObjectivePolicy = ClimateChange, code = "climate")
            )
        )

        every { programmePriorityRepository.save(any<ProgrammePriority>()) } returns ProgrammePriority(
            id = 20,
            code = toUpdate.code!!,
            title = toUpdate.title!!,
            objective = toUpdate.objective!!,
            programmePriorityPolicies = setOf(
                ProgrammePriorityPolicy(programmeObjectivePolicy = ClimateChange, code = "climate"),
                ProgrammePriorityPolicy(programmeObjectivePolicy = WaterManagement, code = "water"),
                ProgrammePriorityPolicy(programmeObjectivePolicy = SmartEnergy, code = "smart")
            )
        )

        val expectedResult = OutputProgrammePriority(
            id = 20,
            code = "existing prio",
            title = "Existing priority",
            objective = PO2,
            programmePriorityPolicies = listOf(
                OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = SmartEnergy, code = "smart"),
                OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = ClimateChange, code = "climate"),
                OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = WaterManagement, code = "water")
            )
        )

        assertThat(programmePriorityService.update(toUpdate))
            .isEqualTo(expectedResult)
    }

    @Test
    fun `update wrong PO policies`() {
        val toUpdate = InputProgrammePriorityUpdate(
            id = 20,
            code = "existing prio",
            title = "Existing priority",
            objective = PO2,
            programmePriorityPolicies = setOf(
                InputProgrammePriorityPolicy(programmeObjectivePolicy = WaterManagement, code = "water"),
                InputProgrammePriorityPolicy(programmeObjectivePolicy = SocialInnovation, code = "social")
            )
        )

        val exception = assertThrows<I18nValidationException> { programmePriorityService.update(toUpdate) }

        val expectedException = I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "programme.priority.priorityPolicies.should.not.be.of.different.objectives"
        )
        assertThat(exception).isEqualTo(expectedException)
    }

    @Test
    fun delete() {
        val existingEntity = ProgrammePriority(
            id = 20,
            code = "existing prio",
            title = "Existing priority",
            objective = PO1,
            programmePriorityPolicies = setOf(
                ProgrammePriorityPolicy(programmeObjectivePolicy = IndustrialTransition, code = "indu"),
                ProgrammePriorityPolicy(programmeObjectivePolicy = Growth, code = "growth")
            )
        )

        every { programmePriorityRepository.findById(eq(4)) } returns Optional.of(existingEntity)
        every { programmePriorityRepository.delete(eq(existingEntity)) } returnsArgument 0

        programmePriorityService.delete(4)
    }

    @Test
    fun `delete not existing`() {
        every { programmePriorityRepository.findById(eq(-9)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { programmePriorityService.delete(-9) }
    }

    @Test
    fun getFreePrioritiesWithPolicies() {
        val alreadyAssigned = ProgrammeObjectivePolicy.values().toMutableSet()
        alreadyAssigned.removeAll(setOf(AdvancedTechnologies, Digitalization, EnergyEfficiency))
        every { programmePriorityPolicyRepository.findAll() } returns alreadyAssigned.map { ProgrammePriorityPolicy(it, it.toString()) }

        val expectedResult = mapOf(
            PO1 to listOf(AdvancedTechnologies, Digitalization),
            PO2 to listOf(EnergyEfficiency)
        )
        assertThat(programmePriorityService.getFreePrioritiesWithPolicies())
            .isEqualTo(expectedResult)
    }

    @Test
    fun `getByCode and getByTitle`() {
        val toReturn = ProgrammePriority(
            id = 17,
            code = "existing",
            title = "Existing programme",
            objective = PO2,
            programmePriorityPolicies = emptySet()
        )
        every { programmePriorityRepository.findFirstByCode(eq("existing")) } returns toReturn
        every { programmePriorityRepository.findFirstByTitle(eq("existing title")) } returns toReturn

        val expectedProgramme = OutputProgrammePriority(
            id = 17,
            code = "existing",
            title = "Existing programme",
            objective = PO2,
            programmePriorityPolicies = emptyList()
        )

        assertThat(programmePriorityService.getByCode("existing")).isEqualTo(expectedProgramme)
        assertThat(programmePriorityService.getByTitle("existing title")).isEqualTo(expectedProgramme)
    }

    @Test
    fun `getByCode and getBytitle not existing`() {
        every { programmePriorityRepository.findFirstByCode(eq("not-existing")) } returns null
        every { programmePriorityRepository.findFirstByTitle(eq("not-existing title")) } returns null
        assertThat(programmePriorityService.getByCode("not-existing")).isNull()
        assertThat(programmePriorityService.getByTitle("not-existing title")).isNull()
    }

    @Test
    fun getPriorityPolicyByCode() {
        every { programmePriorityPolicyRepository.findFirstByCode("code-used") } returns
            ProgrammePriorityPolicy(programmeObjectivePolicy = AdvancedTechnologies, code = "code-used")
        every { programmePriorityPolicyRepository.findFirstByCode("code-not-used") } returns null

        assertThat(programmePriorityService.getPriorityPolicyByCode("code-used"))
            .isEqualTo(OutputProgrammePriorityPolicySimple(programmeObjectivePolicy = AdvancedTechnologies, code = "code-used"))
        assertThat(programmePriorityService.getPriorityPolicyByCode("code-not-used")).isNull()
    }

    @Test
    fun getPriorityIdForPolicyIfExists() {
        every { programmePriorityPolicyRepository.getPriorityIdForPolicyIfExists(eq(AdvancedTechnologies)) } returns 12L
        every { programmePriorityPolicyRepository.getPriorityIdForPolicyIfExists(eq(Digitalization)) } returns null

        assertThat(programmePriorityService.getPriorityIdForPolicyIfExists(AdvancedTechnologies)).isEqualTo(12L)
        assertThat(programmePriorityService.getPriorityIdForPolicyIfExists(Digitalization)).isNull()
    }

}
