package io.cloudflight.jems.server.project.service.cofinancing.model

data class ProjectCoFinancingOverview(
    val projectManagementCoFinancing: ProjectCoFinancingCategoryOverview,
    val projectSpfCoFinancing: ProjectCoFinancingCategoryOverview
)
