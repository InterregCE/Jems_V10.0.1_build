package io.cloudflight.jems.server.project.service.report.project.base.getProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
) : GetProjectReportInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportException::class)
    override fun findById(projectId: Long, reportId: Long): ProjectReport =
        reportPersistence.getReportById(projectId, reportId = reportId).let { report ->
            val periods = report.getProjectPeriods()
            report.toServiceModel { periodNumber -> periods.first { it.number == periodNumber } }
        }

    private fun ProjectReportModel.getProjectPeriods() =
        projectPersistence.getProjectPeriods(projectId, linkedFormVersion)

    fun getProjectPeriods(projectId: Long, linkedFormVersion: String?) = projectPersistence.getProjectPeriods(projectId, linkedFormVersion)


}
