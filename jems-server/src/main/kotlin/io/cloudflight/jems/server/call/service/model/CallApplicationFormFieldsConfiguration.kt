package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallType

data class CallApplicationFormFieldsConfiguration(
    val callType: CallType,
    var applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>
)
