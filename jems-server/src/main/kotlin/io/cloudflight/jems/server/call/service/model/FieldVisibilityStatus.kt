package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallType


enum class FieldVisibilityStatus {
    NONE,
    STEP_ONE_AND_TWO,
    STEP_TWO_ONLY;

    companion object {
        fun getDefaultFieldVisibilityStatus(
            callType: CallType,
            formFieldSetting: ApplicationFormFieldSetting
        ): FieldVisibilityStatus {
            val formFieldHasSPFVisibilitySettings = formFieldSetting.validSPFVisibilityStatusSet.isNotEmpty()
            val spfFieldVisibleByDefault = (callType == CallType.SPF) && formFieldHasSPFVisibilitySettings
                && ApplicationFormFieldSetting.getSpfFormFieldsVisibleByDefault().contains(formFieldSetting.id)

            val formFieldVisibilitySettings = when {
                (callType == CallType.SPF) && formFieldHasSPFVisibilitySettings -> formFieldSetting.validSPFVisibilityStatusSet
                else -> formFieldSetting.validStandardVisibilityStatusSet
            }

            return when {
                spfFieldVisibleByDefault -> STEP_TWO_ONLY
                formFieldVisibilitySettings.contains(NONE) -> NONE
                formFieldVisibilitySettings.contains(STEP_TWO_ONLY) -> STEP_TWO_ONLY
                else -> STEP_ONE_AND_TWO
            }
        }
    }
}
