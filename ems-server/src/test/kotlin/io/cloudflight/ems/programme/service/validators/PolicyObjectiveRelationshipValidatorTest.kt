package io.cloudflight.ems.programme.service.validators

import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityPolicy
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO1
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy.ClimateChange
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy.Digitalization
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy.EnergyEfficiency
import io.cloudflight.ems.api.programme.validator.PolicyObjectiveRelationshipValidator
import io.cloudflight.ems.programme.service.validator.PolicyObjectiveRelationshipValidatorImpl
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PolicyObjectiveRelationshipValidatorTest {

    lateinit var policyObjectiveRelationshipValidator: PolicyObjectiveRelationshipValidator

    @BeforeEach
    fun setup() {
        policyObjectiveRelationshipValidator = PolicyObjectiveRelationshipValidatorImpl()
    }

    @Test
    fun isValid() {
        val testData = setOf(
            InputProgrammePriorityPolicy(programmeObjectivePolicy = AdvancedTechnologies),
            InputProgrammePriorityPolicy(programmeObjectivePolicy = Digitalization)
        )
        assertTrue(policyObjectiveRelationshipValidator.isValid(testData, PO1),
            "all of these are ok, because they belong to same objective $PO1")
    }

    @Test
    fun oneIsInvalid() {
        val testData = setOf(
            InputProgrammePriorityPolicy(programmeObjectivePolicy = AdvancedTechnologies),
            InputProgrammePriorityPolicy(programmeObjectivePolicy = ClimateChange)
        )
        assertFalse(policyObjectiveRelationshipValidator.isValid(testData, PO1),
            "${ClimateChange} belongs to ${ClimateChange.objective}, not to $PO1")
    }

    @Test
    fun everythingIsInvalid() {
        val testData = setOf(
            InputProgrammePriorityPolicy(programmeObjectivePolicy = EnergyEfficiency),
            InputProgrammePriorityPolicy(programmeObjectivePolicy = ClimateChange)
        )
        assertFalse(policyObjectiveRelationshipValidator.isValid(testData, PO1),
            "none of those belong to $PO1")
    }

    @Test
    fun isValidEmpty() {
        assertTrue(policyObjectiveRelationshipValidator.isValid(emptySet(), PO1),
            "empty set is valid")
    }

}
