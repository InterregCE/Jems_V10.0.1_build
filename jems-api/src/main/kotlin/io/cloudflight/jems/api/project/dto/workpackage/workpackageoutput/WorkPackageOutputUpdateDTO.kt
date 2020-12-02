package io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput

data class WorkPackageOutputUpdateDTO(

    val outputNumber: Int,
    val programmeOutputIndicatorId: Long? = null,
    val title: String? = null,
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: String? = null

)