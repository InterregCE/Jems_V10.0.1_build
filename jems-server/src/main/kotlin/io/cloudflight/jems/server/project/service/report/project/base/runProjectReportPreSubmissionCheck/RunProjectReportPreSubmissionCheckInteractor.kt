package io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult

interface RunProjectReportPreSubmissionCheckInteractor {

    fun preCheck(projectId: Long, reportId: Long): PreConditionCheckResult

}
