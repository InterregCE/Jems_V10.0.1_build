package io.cloudflight.jems.server.call.service.validator

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.OutputCall
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.validator.UniqueCallNameValidator
import io.cloudflight.jems.server.call.service.CallService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class UniqueCallNameValidatorTest {

    @MockK
    lateinit var callService: CallService

    lateinit var uniqueCallNameValidator: UniqueCallNameValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        uniqueCallNameValidator = UniqueCallNameValidatorImpl(callService)
    }

    @Test
    fun `null is valid`() {
        assertTrue(uniqueCallNameValidator.isValid(null))
    }

    @Test
    fun `non-existing is valid`() {
        every { callService.findOneByName(eq("non-existing")) } returns null
        assertTrue(uniqueCallNameValidator.isValid("non-existing"))
    }

    @Test
    fun `existing is not valid`() {
        every { callService.findOneByName(eq("existing")) } returns OutputCall(id = 1, name = "test call", priorityPolicies = emptyList(), strategies = emptyList(), funds = emptyList(), status = CallStatus.PUBLISHED, startDate = ZonedDateTime.now(), endDate = ZonedDateTime.now(), lengthOfPeriod = 1, flatRates = FlatRateSetupDTO())
        assertFalse(uniqueCallNameValidator.isValid("existing"))
    }

}
