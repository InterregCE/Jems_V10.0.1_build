package io.cloudflight.jems.server.call.service.model

enum class ApplicationFormFieldSetting(val id: String, val validVisibilityStatusSet: Set<FieldVisibilityStatus>) {

    PROJECT_ACRONYM("project.application.form.field.project.acronym", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_TITLE(
        "project.application.form.field.project.title",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_DURATION("project.application.form.field.project.duration", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_PRIORITY("project.application.form.field.project.priority", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),
    PROJECT_OBJECTIVE(
        "project.application.form.field.project.objective",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)
    ),
    PROJECT_SUMMARY("project.application.form.field.project.summary", setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO)),

    PROJECT_RESULTS_PROGRAMME_RESULT_INDICATOR_AMD_MEASUREMENT_UNIT(
        "project.results.result.indicator.and.measurement.unit",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_TARGET_VALUE(
        "project.results.result.target.value",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_DELIVERY_PERIOD(
        "project.results.result.delivery.period",
        setOf(FieldVisibilityStatus.NONE, FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    ),
    PROJECT_RESULTS_DESCRIPTION(
        "project.results.result.description",
        setOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY)
    );

    companion object {
        fun getValidVisibilityStatusSetById(id: String): Set<FieldVisibilityStatus> =
            values().find { it.id == id }?.validVisibilityStatusSet ?: emptySet()

        fun getDefaultApplicationFormFieldConfigurations() =
            values().map {

                    val defaultVisibilityStatus =
                        when {
                            it.validVisibilityStatusSet.contains(FieldVisibilityStatus.NONE) -> FieldVisibilityStatus.NONE
                            it.validVisibilityStatusSet.contains(FieldVisibilityStatus.STEP_TWO_ONLY) -> FieldVisibilityStatus.STEP_TWO_ONLY
                            else -> FieldVisibilityStatus.STEP_ONE_AND_TWO
                        }
                    ApplicationFormFieldConfiguration(it.id, defaultVisibilityStatus)
            }.toMutableSet()
    }

}
