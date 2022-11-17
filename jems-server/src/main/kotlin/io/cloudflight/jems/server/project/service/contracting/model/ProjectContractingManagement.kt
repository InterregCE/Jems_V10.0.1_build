package io.cloudflight.jems.server.project.service.contracting.model

data class ProjectContractingManagement(
    val projectId: Long,
    val managementType: ManagementType,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val telephone: String? = null
)
