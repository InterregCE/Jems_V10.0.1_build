package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.plugin.contract.models.call.CallDetailData
import io.cloudflight.jems.plugin.contract.models.call.CallStatusData
import io.cloudflight.jems.plugin.contract.models.call.flatrate.FlatRateSetupData
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

const val PROJECT_ID = 612L

internal class CallDataProviderImplTest : UnitTest() {

    @MockK
    lateinit var callPersistence: CallPersistence

    @MockK
    lateinit var programmeLanguagePersistence: ProgrammeLanguagePersistence

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
            type = CallType.STANDARD,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(InputTranslation(SystemLanguage.EN, "description")),
            objectives = listOf(),
            strategies = sortedSetOf(),
            funds = sortedSetOf(),
            flatRates = sortedSetOf(),
            lumpSums = listOf(),
            unitCosts = listOf(),
            applicationFormFieldConfigurations = applicationFormFieldConfigurations,
            preSubmissionCheckPluginKey = null
        )

        private val programmeLanguages = listOf(
            ProgrammeLanguage(code = SystemLanguage.EN, ui = true, fallback = true, input = true),
            ProgrammeLanguage(code = SystemLanguage.DE, ui = true, fallback = false, input = true),
            ProgrammeLanguage(code = SystemLanguage.FR, ui = true, fallback = false, input = false)
        )
    }

    @Test
    fun `should return call data by call id`() {
        every { programmeLanguagePersistence.getLanguages() } returns programmeLanguages
        every { callPersistence.getCallById(CALL_ID) } returns callDetail

        assertThat(callDataProviderImpl.getCallData(CALL_ID)).isEqualTo(
            CallDetailData(
                id = callDetail.id,
                name = callDetail.name,
                isAdditionalFundAllowed = callDetail.isAdditionalFundAllowed,
                status = CallStatusData.valueOf(callDetail.status.name),
                startDateTime = callDetail.startDate,
                endDateTimeStep1 = callDetail.endDateStep1,
                endDateTime = callDetail.endDate,
                lengthOfPeriod = callDetail.lengthOfPeriod,
                description = setOf(InputTranslationData(SystemLanguageData.EN, "description")),
                objectives = listOf(),
                strategies = listOf(),
                funds = listOf(),
                flatRates = FlatRateSetupData(null, null, null, null, null),
                lumpSums = listOf(),
                unitCosts = listOf(),
                applicationFormFieldConfigurations = applicationFormFieldConfigurations.toDataModel(),
                inputLanguages = setOf(SystemLanguageData.EN, SystemLanguageData.DE)
            )
        )
    }

    @Test
    fun `should return call data by project id`() {
        every { programmeLanguagePersistence.getLanguages() } returns programmeLanguages
        every { callPersistence.getCallByProjectId(PROJECT_ID) } returns callDetail

        assertThat(callDataProviderImpl.getCallDataByProjectId(PROJECT_ID)).isEqualTo(
            CallDetailData(
                id = callDetail.id,
                name = callDetail.name,
                isAdditionalFundAllowed = callDetail.isAdditionalFundAllowed,
                status = CallStatusData.valueOf(callDetail.status.name),
                startDateTime = callDetail.startDate,
                endDateTimeStep1 = callDetail.endDateStep1,
                endDateTime = callDetail.endDate,
                lengthOfPeriod = callDetail.lengthOfPeriod,
                description = setOf(InputTranslationData(SystemLanguageData.EN, "description")),
                objectives = listOf(),
                strategies = listOf(),
                funds = listOf(),
                flatRates = FlatRateSetupData(null, null, null, null, null),
                lumpSums = listOf(),
                unitCosts = listOf(),
                applicationFormFieldConfigurations = applicationFormFieldConfigurations.toDataModel(),
                inputLanguages = setOf(SystemLanguageData.EN, SystemLanguageData.DE)
            )
        )
    }
}
