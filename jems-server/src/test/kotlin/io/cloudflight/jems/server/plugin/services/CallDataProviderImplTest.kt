package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.service.PROJECT_ID
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class CallDataProviderImplTest : UnitTest() {

    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var callDataProviderImpl: CallDataProviderImpl

    companion object {
        private const val CALL_ID = 1L

        private val applicationFormFieldConfigurations = mutableSetOf(
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            ),
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_TITLE.id,
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            ),
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            )
        )

        private val callDetail = CallDetail(
            id = CALL_ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(),
            objectives = listOf(),
            strategies = sortedSetOf(),
            funds = listOf(),
            flatRates = sortedSetOf(),
            lumpSums = listOf(),
            unitCosts = listOf(),
            applicationFormFieldConfigurations = applicationFormFieldConfigurations
        )
    }

    @Test
    fun `should return call data by call id`() {
        every { callPersistence.getCallById(CALL_ID) } returns callDetail
        assertThat(callDataProviderImpl.getCallData(CALL_ID)).isEqualTo(
            callDetail.toDataModel()
        )
    }

    @Test
    fun `should return call data by project id`() {
        every { callPersistence.getCallByProjectId(PROJECT_ID) } returns callDetail
        assertThat(callDataProviderImpl.getCallDataByProjectId(PROJECT_ID)).isEqualTo(
            callDetail.toDataModel()
        )
    }
}
