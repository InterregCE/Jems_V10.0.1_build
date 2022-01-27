package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.validator.StartBeforeEndValidator
import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.server.UnitTest
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZonedDateTime

internal class StartDateBeforeEndDateValidatorImplTest : UnitTest() {

    companion object {
        fun programmeDataUpdateRequest(firstYear: Int?, lastYear: Int?) = ProgrammeDataUpdateRequestDTO(
            cci = "cci",
            title = "title",
            version = "version",
            firstYear = firstYear,
            lastYear = lastYear,
            eligibleFrom = LocalDate.of(2020, 1, 1),
            eligibleUntil = LocalDate.of(2021, 1, 1),
            commissionDecisionNumber = "cd number",
            commissionDecisionDate = LocalDate.of(2020, 1, 1),
            programmeAmendingDecisionNumber = "pad number",
            programmeAmendingDecisionDate = LocalDate.of(2020, 1, 1),
            projectIdProgrammeAbbreviation = "NL-DE_",
            projectIdUseCallId = true
        )
    }

    private val validator = StartDateBeforeEndDateValidatorImpl()
    private val startBeforeEndValidator = StartBeforeEndValidator(validator)

    @Test
    fun `should return correct validation result when start is not before until date`() {
        val dateNow = ZonedDateTime.now()
        val dateYesterday = ZonedDateTime.now().minusDays(1)
        val dateTomorrow = ZonedDateTime.now().plusDays(1)

        assertThat(validator.isEndNotBeforeStart(dateTomorrow, dateYesterday)).isFalse
        assertThat(validator.isEndNotBeforeStart(dateYesterday, dateTomorrow)).isTrue
        assertThat(validator.isEndNotBeforeStart(dateNow, dateNow)).isTrue
    }

    @Test
    fun `should return correct validation on test ProgrammeUpdateRequestData`() {
        val context: ConstraintValidatorContextImpl = mockk()

        assertThat(startBeforeEndValidator.isValid(programmeDataUpdateRequest(firstYear = 2014, lastYear = 2016), context)).isTrue
        assertThat(startBeforeEndValidator.isValid(programmeDataUpdateRequest(firstYear = 2016, lastYear = 2014), context)).isFalse
        assertThat(startBeforeEndValidator.isValid(programmeDataUpdateRequest(firstYear = null, lastYear = null), context)).isTrue
        assertThat(startBeforeEndValidator.isValid(programmeDataUpdateRequest(firstYear = 2016, lastYear = 2016), context)).isTrue
    }
}
