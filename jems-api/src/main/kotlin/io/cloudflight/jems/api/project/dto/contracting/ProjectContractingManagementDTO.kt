package io.cloudflight.jems.api.project.dto.contracting

data class ProjectContractingManagementDTO(
    val projectId: Long,
    val managementType: ManagementTypeDTO,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val telephone: String? = null
)
