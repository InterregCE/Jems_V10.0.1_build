package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.project.dto.report.project.verification.ProjectReportVerificationClarificationDTO
import io.cloudflight.jems.api.project.dto.report.project.verification.ProjectReportVerificationConclusionDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationApi
import io.cloudflight.jems.server.project.controller.report.project.toDto
import io.cloudflight.jems.server.project.controller.report.project.toModel
import io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification.GetProjectReportVerificationClarificationInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationConclusion.GetProjectReportVerificationConclusionInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification.UpdateProjectReportVerificationClarificationInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion.UpdateProjectReportVerificationConclusionInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportVerificationController(
    private val getProjectReportVerificationConclusion: GetProjectReportVerificationConclusionInteractor,
    private val updateProjectReportVerificationConclusion: UpdateProjectReportVerificationConclusionInteractor,
    private val getProjectReportVerificationClarification: GetProjectReportVerificationClarificationInteractor,
    private val updateProjectReportVerificationClarification: UpdateProjectReportVerificationClarificationInteractor,
): ProjectReportVerificationApi {

    override fun getReportVerificationConclusion(
        projectId: Long, reportId: Long
    ): ProjectReportVerificationConclusionDTO =
        getProjectReportVerificationConclusion.getVerificationConclusion(projectId = projectId, reportId = reportId)
            .toDto()

    override fun updateReportVerificationConclusion(
        projectId: Long,
        reportId: Long,
        conclusion: ProjectReportVerificationConclusionDTO
    ): ProjectReportVerificationConclusionDTO =
        updateProjectReportVerificationConclusion.updateVerificationConclusion(
            projectId = projectId,
            reportId = reportId,
            conclusion = conclusion.toModel()
        ).toDto()

    override fun getReportVerificationClarificationRequests(
        projectId: Long,
        reportId: Long
    ): List<ProjectReportVerificationClarificationDTO> =
        getProjectReportVerificationClarification.getClarifications(projectId = projectId,reportId = reportId).map { it.toDto() }

    override fun updateReportVerificationClarifications(
        projectId: Long,
        reportId: Long,
        clarifications: List<ProjectReportVerificationClarificationDTO>
    ): List<ProjectReportVerificationClarificationDTO> = updateProjectReportVerificationClarification.updateClarifications(
        projectId = projectId,
        reportId = reportId,
        clarifications = clarifications.map { it.toModel() }
    ).map { it.toDto() }

}
