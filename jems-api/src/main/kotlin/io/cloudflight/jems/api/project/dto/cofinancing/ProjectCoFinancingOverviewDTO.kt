package io.cloudflight.jems.api.project.dto.cofinancing

data class ProjectCoFinancingOverviewDTO(
    val projectManagementCoFinancing: ProjectCoFinancingCategoryOverviewDTO,
    val projectSpfCoFinancing: ProjectCoFinancingCategoryOverviewDTO
)
