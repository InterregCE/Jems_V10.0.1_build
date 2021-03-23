package io.cloudflight.jems.server.programme.service.priority.create_priority

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.ISO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.GreenInfrastructure
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.SmartEnergy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.getStringOfLength
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.programme.service.priority.testPriority
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreatePriorityInteractorTest {

    @MockK
    lateinit var persistence: ProgrammePriorityPersistence

    @MockK
    lateinit var auditService: AuditService

    @InjectMockKs
    private lateinit var createPriority: CreatePriority

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
    fun `createPriority - long code or long title`() {
        val priorityWithLongCode = testPriority.copy(code = getStringOfLength(51))
        var ex = assertThrows<I18nValidationException> { createPriority.createPriority(priorityWithLongCode) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.code.size.too.long")

        val priorityWithLongTitle = testPriority.copy(
            title = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(301)))
        )
        ex = assertThrows { createPriority.createPriority(priorityWithLongTitle) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.title.size.too.long")
    }

    @Test
    fun `createPriority - wrong code (long or empty)`() {
        testWrongCode(getStringOfLength(51))
        testWrongCode(" ")
        testWrongCode("")
        testWrongCode("\t")
    }

    private fun testWrongCode(code: String) {
        val priority = testPriority.copy(code = code)
        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.code.size.too.long")
    }

    @Test
    fun `createPriority - wrong title (long or empty)`() {
        testWrongTitle(getStringOfLength(301))
    }

    private fun testWrongTitle(title: String) {
        val priority = testPriority.copy(title = setOf(InputTranslation(SystemLanguage.EN, title)))
        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.title.size.too.long")
    }

    @Test
    fun `createPriority - no specific objective`() {
        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(testPriority.copy(specificObjectives = emptyList())) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjectives.empty")
    }

    @Test
    fun `createPriority - wrong specific objective code (long or empty)`() {
        testWrongSpecificObjectiveCode(getStringOfLength(51))
        testWrongSpecificObjectiveCode(" ")
        testWrongSpecificObjectiveCode("")
        testWrongSpecificObjectiveCode("\n")
    }

    private fun testWrongSpecificObjectiveCode(code: String, so: ProgrammeObjectivePolicy = RenewableEnergy) {
        val priority = testPriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = so,
                    code = code
                )
            )
        )
        val ex = assertThrows<I18nValidationException> { createPriority.createPriority(priority) }
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(
            I18nFieldError(
                i18nKey = "programme.priority.specificObjective.code.size.too.long.or.empty",
                i18nArguments = listOf(so.name)
            )
        )
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
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(I18nFieldError(
            i18nKey = "programme.priority.specificObjective.objectivePolicy.already.in.use",
            i18nArguments = listOf(RenewableEnergy.name)
        ))
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
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(I18nFieldError(
            i18nKey = "programme.priority.specificObjective.code.already.in.use",
            i18nArguments = listOf("RE")
        ))
    }

}
