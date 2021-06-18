package io.cloudflight.jems.server.call.service.model

data class ApplicationFormFieldConfiguration(
    val id: String,
    val visibilityStatus: FieldVisibilityStatus
) {
    fun getValidVisibilityStatusSet(): Set<FieldVisibilityStatus> =
        ApplicationFormFieldSetting.getValidVisibilityStatusSetById(id)
}
