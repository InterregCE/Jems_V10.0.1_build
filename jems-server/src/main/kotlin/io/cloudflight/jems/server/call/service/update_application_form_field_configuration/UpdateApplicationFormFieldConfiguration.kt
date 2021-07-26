package io.cloudflight.jems.server.call.service.update_application_form_field_configuration

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateApplicationFormFieldConfiguration(private val persistence: CallPersistence) :
    UpdateApplicationFormFieldConfigurationsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdateApplicationFormFieldConfigurationsException::class)
    override fun update(
        callId: Long,
        applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>
    ) =
        ifConfigurationIsValid(applicationFormFieldConfigurations, callId).run {
            resetVisibilityForFieldsThatDependsOnBudgetSetting(applicationFormFieldConfigurations)
            persistence.saveApplicationFormFieldConfigurations(callId, applicationFormFieldConfigurations)
        }


    private fun ifConfigurationIsValid(
        applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>,
        callId: Long
    ) {

        if (applicationFormFieldConfigurations.any {
                !ApplicationFormFieldSetting.getValidVisibilityStatusSetById(it.id).contains(it.visibilityStatus)
            }) throw InvalidFieldStatusException()

        val existingCall = persistence.getCallById(callId)

        if (existingCall.isPublished() &&
            applicationFormFieldConfigurations.any {
                fieldVisibilityHasChangedToNone(it, existingCall) ||
                    fieldVisibilityHasChangedFromOneAndTwoStepToStepTwoOnly(it, existingCall)
            }
        ) throw InvalidFieldVisibilityChangeWhenCallIsPublishedException()
    }

    private fun resetVisibilityForFieldsThatDependsOnBudgetSetting(applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>): Unit =
        with(applicationFormFieldConfigurations.first { it.id == ApplicationFormFieldSetting.PARTNER_BUDGET_AND_CO_FINANCING.id }) {
            val dependentFieldIds = ApplicationFormFieldSetting.getFieldsThatDependsOnBudgetSetting()
            applicationFormFieldConfigurations.filter { dependentFieldIds.contains(it.id) && it.visibilityStatus != FieldVisibilityStatus.NONE }
                .forEach { fieldConfig ->
                    fieldConfig.visibilityStatus = this.visibilityStatus
                }
        }


    private fun fieldVisibilityHasChangedToNone(
        newConfig: ApplicationFormFieldConfiguration,
        existingCall: CallDetail
    ) =
        newConfig.visibilityStatus == FieldVisibilityStatus.NONE &&
            currentConfigFor(newConfig.id, existingCall)?.visibilityStatus != FieldVisibilityStatus.NONE

    private fun fieldVisibilityHasChangedFromOneAndTwoStepToStepTwoOnly(
        newConfig: ApplicationFormFieldConfiguration,
        existingCall: CallDetail
    ) =
        newConfig.visibilityStatus == FieldVisibilityStatus.STEP_TWO_ONLY &&
            currentConfigFor(
                newConfig.id,
                existingCall
            )?.visibilityStatus == FieldVisibilityStatus.STEP_ONE_AND_TWO

    private fun currentConfigFor(fieldId: String, existingCall: CallDetail): ApplicationFormFieldConfiguration? =
        existingCall.applicationFormFieldConfigurations.firstOrNull { existingConfig -> existingConfig.id == fieldId }

}
