package io.cloudflight.jems.server.project.service.workpackage.output.model

data class WorkPackageOutput(
    val outputNumber: Int = 0,
    val translatedValues: Set<WorkPackageOutputTranslatedValue> = emptySet(),
    val periodNumber: Int? = null,
    val programmeOutputIndicatorId: Long? = null,
    val targetValue: String? = null
)
