package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview

interface GetProjectCoFinancingOverviewInteractor {
    fun getProjectCoFinancingOverview(projectId: Long, version: String?): ProjectCoFinancingOverview
}
