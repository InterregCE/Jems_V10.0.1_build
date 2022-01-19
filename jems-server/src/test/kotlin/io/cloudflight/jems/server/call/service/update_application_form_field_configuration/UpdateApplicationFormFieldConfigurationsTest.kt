package io.cloudflight.jems.server.call.service.update_application_form_field_configuration

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class UpdateApplicationFormFieldConfigurationsTest : UnitTest() {

    private val CALL_ID = 1L
    private val fieldsThatDependsOnBudget = ApplicationFormFieldSetting.getFieldsThatDependsOnBudgetSetting()
    private val applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration> = mutableSetOf(
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
        ),
        ApplicationFormFieldConfiguration(
            id = ApplicationFormFieldSetting.PARTNER_BUDGET_AND_CO_FINANCING.id,
            visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
        ),
        *fieldsThatDependsOnBudget.map {
            ApplicationFormFieldConfiguration(
                id = it,
                visibilityStatus = FieldVisibilityStatus.STEP_TWO_ONLY
            )
        }.toTypedArray()
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
        description = setOf(),
        objectives = listOf(),
        strategies = sortedSetOf(),
        funds = sortedSetOf(),
        flatRates = sortedSetOf(),
        lumpSums = listOf(),
        unitCosts = listOf(),
        applicationFormFieldConfigurations = applicationFormFieldConfigurations,
        preSubmissionCheckPluginKey = null
    )


    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var updateApplicationFormConfiguration: UpdateApplicationFormFieldConfiguration

    @Test
    fun `update application form field configuration`() {
        every {
            persistence.saveApplicationFormFieldConfigurations(
                CALL_ID,
                applicationFormFieldConfigurations
            )
        } returns callDetail
        every { persistence.getCallById(CALL_ID) } returns callDetail

        val result = updateApplicationFormConfiguration.update(CALL_ID, applicationFormFieldConfigurations)

        assertThat(result).isEqualTo(callDetail)
    }

    @Test
    fun `update application form field configurations - fail on invalid`() {
        val invalidApplicationFormConfiguration = mutableSetOf(
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            ),
            ApplicationFormFieldConfiguration(
                id = "test",
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            )
        )
        every {
            persistence.saveApplicationFormFieldConfigurations(
                CALL_ID,
                invalidApplicationFormConfiguration
            )
        } returns callDetail

        assertThrows<InvalidFieldStatusException> {
            updateApplicationFormConfiguration.update(
                CALL_ID,
                invalidApplicationFormConfiguration
            )
        }
    }

    @Test
    fun `should throw InvalidFieldVisibilityChangeWhenCallIsPublishedException when at least one field visibility is changed to none and call is published`() {
        val invalidApplicationFormConfiguration = mutableSetOf(
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                visibilityStatus = FieldVisibilityStatus.NONE
            )
        )
        every { persistence.getCallById(CALL_ID) } returns callDetail.copy(status = CallStatus.PUBLISHED)
        every {
            persistence.saveApplicationFormFieldConfigurations(
                CALL_ID,
                invalidApplicationFormConfiguration
            )
        } returns callDetail

        assertThrows<InvalidFieldVisibilityChangeWhenCallIsPublishedException> {
            updateApplicationFormConfiguration.update(
                CALL_ID,
                invalidApplicationFormConfiguration
            )
        }
    }

    @Test
    fun `should throw InvalidFieldVisibilityChangeWhenCallIsPublishedException when at least one field visibility is changed from one-and-two step to step-two-only when call is published`() {
        val invalidApplicationFormConfiguration = mutableSetOf(
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                visibilityStatus = FieldVisibilityStatus.STEP_TWO_ONLY
            )
        )
        every { persistence.getCallById(CALL_ID) } returns callDetail.copy(status = CallStatus.PUBLISHED)
        every {
            persistence.saveApplicationFormFieldConfigurations(
                CALL_ID,
                invalidApplicationFormConfiguration
            )
        } returns callDetail

        assertThrows<InvalidFieldVisibilityChangeWhenCallIsPublishedException> {
            updateApplicationFormConfiguration.update(
                CALL_ID,
                invalidApplicationFormConfiguration
            )
        }
    }

    @Test
    fun `should set visibility status of fields that are dependent on the budget and co-financing to budget and co-financing visibility when user updates the visibility of budget and co-financing`() {
        val slot = slot<MutableSet<ApplicationFormFieldConfiguration>>()
        every { persistence.saveApplicationFormFieldConfigurations(CALL_ID, capture(slot)) } returns callDetail
        every { persistence.getCallById(CALL_ID) } returns callDetail

        val result = updateApplicationFormConfiguration.update(CALL_ID, applicationFormFieldConfigurations)

        val budgetSetting =
            applicationFormFieldConfigurations.first { it.id == ApplicationFormFieldSetting.PARTNER_BUDGET_AND_CO_FINANCING.id }
        assertThat(slot.captured).containsAll(
            applicationFormFieldConfigurations.map {
                if (fieldsThatDependsOnBudget.contains(it.id) && it.visibilityStatus != FieldVisibilityStatus.NONE)
                    ApplicationFormFieldConfiguration(it.id, budgetSetting.visibilityStatus)
                else it
            }
        )
        assertThat(result).isEqualTo(callDetail)
    }
}
