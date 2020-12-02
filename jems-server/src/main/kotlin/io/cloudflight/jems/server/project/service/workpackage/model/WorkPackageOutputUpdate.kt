package io.cloudflight.jems.server.project.service.workpackage.model

data class WorkPackageOutputUpdate(

    val outputNumber: Int,
    val programmeOutputIndicatorId: Long? = null,
    val title: String? = null,
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: String? = null

)