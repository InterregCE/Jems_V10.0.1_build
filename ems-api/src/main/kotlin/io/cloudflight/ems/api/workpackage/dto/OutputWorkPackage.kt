package io.cloudflight.ems.api.workpackage.dto

data class OutputWorkPackage (
    val id: Long?,
    val number: Int,
    val name: String,
    val specificObjective: String?,
    val objectiveAndAudience: String?
)
