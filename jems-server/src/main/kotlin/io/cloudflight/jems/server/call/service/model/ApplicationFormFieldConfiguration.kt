package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallType

data class ApplicationFormFieldConfiguration(
    val id: String,
    var visibilityStatus: FieldVisibilityStatus
) {
    fun getValidVisibilityStatusSet(callType: CallType): Set<FieldVisibilityStatus> =
        ApplicationFormFieldSetting.getValidVisibilityStatusSetById(id, callType)
}
