package io.cloudflight.jems.api.project.dto.workpackage

data class OutputWorkPackage (
    val id: Long,
    val number: Int?,
    val name: String?,
    val specificObjective: String?,
    val objectiveAndAudience: String?
)
