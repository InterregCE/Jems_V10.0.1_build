package io.cloudflight.jems.server.project.service.workpackage.output.model

data class WorkPackageOutput(

    val outputNumber: Int,
    val programmeOutputIndicatorId: Long? = null,
    val translatedValues: Set<WorkPackageOutputTranslatedValue> = emptySet(),
    val targetValue: String? = null,
    val periodNumber: Int? = null
    )