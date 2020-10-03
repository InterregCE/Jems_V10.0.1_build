package io.cloudflight.jems.api.workpackage.dto

data class OutputWorkPackage (
    val id: Long,
    val number: Int?,
    val name: String?,
    val specificObjective: String?,
    val objectiveAndAudience: String?
)
