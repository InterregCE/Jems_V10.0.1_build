package io.cloudflight.jems.server.project.service.report.project.closure

import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure

interface ProjectReportProjectClosurePersistence {

    fun getProjectReportProjectClosure(reportId: Long): ProjectReportProjectClosure

    fun updateProjectReportProjectClosure(reportId: Long, updatedProjectClosure: ProjectReportProjectClosure): ProjectReportProjectClosure

    fun deleteProjectReportProjectClosure(reportId: Long)
}
