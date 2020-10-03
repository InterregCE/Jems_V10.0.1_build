package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.ProgrammeObjective
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

internal class PolicyObjectiveValidatorTest {
    @MockK
    lateinit var policyObjectiveRelationshipValidator: PolicyObjectiveRelationshipValidator
    @MockK
    lateinit var validatorContext: ConstraintValidatorContext

    private lateinit var policyObjectiveValidator: PolicyObjectiveValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        policyObjectiveValidator = PolicyObjectiveValidator(policyObjectiveRelationshipValidator)
    }

    @Test
    fun `is valid create objective priority relationship`() {
        val programmePriority = InputProgrammePriorityCreate("code", "title", ProgrammeObjective.PO1, null)
        every { policyObjectiveRelationshipValidator.isValid(programmePriority.programmePriorityPolicies, ProgrammeObjective.PO1) } returns true

        assertThat(policyObjectiveValidator.isValid(programmePriority, validatorContext)).isTrue()
    }

    @Test
    fun `is not valid update objective priority relationship`() {
        val programmePriority = InputProgrammePriorityUpdate(1, "code", "title", ProgrammeObjective.PO1, null)
        every { policyObjectiveRelationshipValidator.isValid(programmePriority.programmePriorityPolicies, ProgrammeObjective.PO1) } returns false

        assertThat(policyObjectiveValidator.isValid(programmePriority, validatorContext)).isFalse()
    }
}
