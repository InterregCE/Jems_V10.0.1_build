package io.cloudflight.jems.server.programme.service.priority.create_priority

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.ISO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.GreenInfrastructure
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SmartEnergy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.getStringOfLength
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.programme.service.priority.testPriority
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreatePriorityInteractorTest {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    @MockK
    lateinit var persistence: ProgrammePriorityPersistence

    @MockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    private lateinit var createPriority: CreatePriority

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `createPriority - valid`() {
        every { persistence.getPriorityIdByCode(testPriority.code) } returns null
        every { persistence.getPriorityIdForPolicyIfExists(any()) } returns null
        every { persistence.getSpecificObjectivesByCodes(setOf("GU", "RE")) } returns emptyList()
        every { persistence.create(any()) } returnsArgument 0
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}

        assertThat(createPriority.createPriority(testPriority)).isEqualTo(testPriority)
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_PRIORITY_ADDED,
            description = "New programme priority 'PO-02' '[InputTranslation(language=EN, translation=PO-02 title)]' was created",
        ))
    }

    @Test
    fun `createPriority - wrong code (empty)`() {
        testWrongCode(" ")
        testWrongCode("")
        testWrongCode("\t")
    }

    private fun testWrongCode(code: String) {
        val priority = testPriority.copy(code = code)
        every { generalValidator.notBlank(code,  "code") } returns inputErrorMap
        assertThrows<AppInputValidationException> { createPriority.createPriority(priority) }
        verify (exactly = 1) { generalValidator.notBlank(code,  "code") }

    }

    @Test
    fun `createPriority - wrong code (long)`() {
        val code = getStringOfLength(51)
        val priority = testPriority.copy(code = code)
        every { generalValidator.maxLength(code, 50, "code") } returns inputErrorMap
        assertThrows<AppInputValidationException> { createPriority.createPriority(priority) }
        verify(exactly = 1) { generalValidator.maxLength(code, 50, "code") }

    }

    @Test
    fun `createPriority - wrong title (long or empty)`() {
        testWrongTitle(getStringOfLength(301))
    }

    private fun testWrongTitle(title: String) {
        val titleSet = setOf(InputTranslation(SystemLanguage.EN, title))
        val priority = testPriority.copy(title = titleSet)
        every { generalValidator.maxLength(titleSet, 300, "title") } returns inputErrorMap
        assertThrows<AppInputValidationException> { createPriority.createPriority(priority) }
        verify(exactly = 1) { generalValidator.maxLength(titleSet, 300, "title") }

    }

    @Test
    fun `createPriority - no specific objective`() {
        every { generalValidator.minSize(emptyList(), 1, "specificObjectives") } returns inputErrorMap
        assertThrows<AppInputValidationException> { createPriority.createPriority(testPriority.copy(specificObjectives = emptyList())) }
        verify(exactly = 1) { generalValidator.minSize(emptyList(), 1, "specificObjectives") }
    }

    @Test
    fun `createPriority - wrong specific objective code (empty)`() {
        testBlankSpecificObjectiveCode(" ")
        testBlankSpecificObjectiveCode("")
        testBlankSpecificObjectiveCode("\n")
    }

    private fun testBlankSpecificObjectiveCode(code: String, so: ProgrammeObjectivePolicy = RenewableEnergy) {
        val priority = testPriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = so,
                    code = code
                )
            )
        )
        every { generalValidator.notBlank(code, "specificObjectives") } returns inputErrorMap

        assertThrows<AppInputValidationException> { createPriority.createPriority(priority) }
        verify(exactly = 1) { generalValidator.notBlank(code, "specificObjectives") }

    }

    @Test
    fun `createPriority - wrong specific objective code (long)`() {
        val code = getStringOfLength(51)
        val priority = testPriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = RenewableEnergy,
                    code = code
                )
            )
        )
        every { generalValidator.maxLength(code, 50, "specificObjectives") } returns inputErrorMap

        assertThrows<AppInputValidationException> { createPriority.createPriority(priority) }
        verify(exactly = 1) { generalValidator.maxLength(code, 50, "specificObjectives") }

    }

    @Test
    fun `createPriority - specific objective does not belong to objective`() {
        assertThat(testPriority.objective).isNotEqualTo(ISO1)
        val priority = testPriority.copy(objective = ISO1)
        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjectives.should.not.be.of.different.objectives")
    }

    @Test
    fun `createPriority - specific objectives have duplicate codes`() {
        val NOT_UNIQUE_CODE = "not-unique-code"

        val priority = testPriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = RenewableEnergy,
                    code = NOT_UNIQUE_CODE
                ),
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = GreenInfrastructure,
                    code = NOT_UNIQUE_CODE
                ),
            )
        )

        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjective.code.should.be.unique")
    }

    @Test
    fun `createPriority - specific objectives have duplicate policies`() {
        val NOT_UNIQUE_POLICY = RenewableEnergy

        val priority = testPriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = NOT_UNIQUE_POLICY,
                    code = "code01"
                ),
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = NOT_UNIQUE_POLICY,
                    code = "code02"
                ),
            )
        )

        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjective.objectivePolicy.should.be.unique")
    }

    @Test
    fun `createPriority - priority code is already in use`() {
        every { persistence.getPriorityIdByCode(testPriority.code) } returns 647
        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(testPriority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.code.already.in.use")
    }

    @Test
    fun `createPriority - specific objective policy is already used by other existing priority`() {
        // priority code and title are not used
        every { persistence.getPriorityIdByCode(testPriority.code) } returns null
        // this one is not used
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns null
        // this one IS ALREADY USED
        every { persistence.getPriorityIdForPolicyIfExists(RenewableEnergy) } returns 25

        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(testPriority) }
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(
            I18nFieldError(
                i18nKey = "programme.priority.specificObjective.objectivePolicy.already.in.use",
                i18nArguments = listOf(RenewableEnergy.name)
            )
        )
    }

    @Test
    fun `createPriority - specific objective code is already used by other existing specific objective`() {
        // priority code and title are not used
        every { persistence.getPriorityIdByCode(testPriority.code) } returns null
        // every policy we try is not used
        every { persistence.getPriorityIdForPolicyIfExists(any()) } returns null
        // here code "RE" is already assigned to existing specific objective
        every { persistence.getSpecificObjectivesByCodes(setOf("RE", "GU")) } returns listOf(
            ProgrammeSpecificObjective(programmeObjectivePolicy = SmartEnergy, code = "RE"),
        )

        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(testPriority) }
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(
            I18nFieldError(
                i18nKey = "programme.priority.specificObjective.code.already.in.use",
                i18nArguments = listOf("RE")
            )
        )
    }

}
