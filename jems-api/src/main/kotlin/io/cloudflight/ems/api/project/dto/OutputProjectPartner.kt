package io.cloudflight.ems.api.project.dto

data class OutputProjectPartner (
    val id: Long?,
    val name: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null
)
