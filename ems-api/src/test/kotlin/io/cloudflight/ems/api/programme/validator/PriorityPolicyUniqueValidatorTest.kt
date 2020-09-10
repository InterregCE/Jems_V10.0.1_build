package io.cloudflight.ems.api.programme.validator

import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityCreate
import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityPolicy
import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityUpdate
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

internal class PriorityPolicyUniqueValidatorTest {
    @MockK
    lateinit var policySelectionValidator: PriorityPolicyUniqueCodeValidator
    @MockK
    lateinit var validatorContext: ConstraintValidatorContext

    lateinit var priorityPolicyUniqueValidator: PriorityPolicyUniqueValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        priorityPolicyUniqueValidator = PriorityPolicyUniqueValidator(policySelectionValidator)
    }

    @Test
    fun `is valid on create without priorities`() {
        val programmePriority = InputProgrammePriorityCreate("code", "title", null, null)
        assertThat(priorityPolicyUniqueValidator.isValid(programmePriority, validatorContext)).isTrue()
    }

    @Test
    fun `is valid on create with unique priorities`() {
        val programmePolicy = InputProgrammePriorityPolicy(ProgrammeObjectivePolicy.AdvancedTechnologies, "code")
        val programmePriorityPolicies = setOf(element = programmePolicy)
        val programmePriority = InputProgrammePriorityCreate("code", "title", null, programmePriorityPolicies)
        every { policySelectionValidator.isPolicyFreeOrBelongsToThisProgramme(ProgrammeObjectivePolicy.AdvancedTechnologies) } returns true
        every { policySelectionValidator.isPolicyCodeUniqueOrNotChanged(programmePolicy) } returns true

        assertThat(priorityPolicyUniqueValidator.isValid(programmePriority, validatorContext)).isTrue()
    }

    @Test
    fun `is not valid on update without unique priorities`() {
        val programmePolicy = InputProgrammePriorityPolicy(ProgrammeObjectivePolicy.AdvancedTechnologies, "code")
        val programmePriorityPolicies = setOf(element = programmePolicy)
        val programmePriority = InputProgrammePriorityUpdate(1, "code", "title", null, programmePriorityPolicies)
        every { policySelectionValidator.isPolicyFreeOrBelongsToThisProgramme(ProgrammeObjectivePolicy.AdvancedTechnologies, 1) } returns false

        assertThat(priorityPolicyUniqueValidator.isValid(programmePriority, validatorContext)).isFalse()
    }
}
