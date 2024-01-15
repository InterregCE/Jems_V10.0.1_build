package io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
): DeleteProjectReportInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(DeleteProjectReportException::class)
    override fun delete(projectId: Long, reportId: Long) {
        val report = reportPersistence.getReportById(projectId, reportId)

        if (report.status.hasBeenSubmitted())
            throw ClosedReportCannotBeDeleted()

        reportPersistence.deleteReport(projectId, reportId = reportId)
        reportPersistence.decreaseNewerReportNumbersIfAllOpen(projectId, report.reportNumber)
        auditPublisher.publishEvent(projectReportDeleted(context = this, report))
    }
}
