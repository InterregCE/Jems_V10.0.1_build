package io.cloudflight.jems.server.call.service.model

data class ApplicationFormFieldConfiguration(
    val id: String,
    var visibilityStatus: FieldVisibilityStatus
) {
    fun getValidVisibilityStatusSet(): Set<FieldVisibilityStatus> =
        ApplicationFormFieldSetting.getValidVisibilityStatusSetById(id)
}
