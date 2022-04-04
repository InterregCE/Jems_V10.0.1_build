package io.cloudflight.jems.server.project.service.report.partner.contribution

import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution

interface ProjectReportContributionPersistence {

    fun getPartnerReportContribution(partnerId: Long, reportId: Long): List<ProjectPartnerReportEntityContribution>

    fun existsByContributionId(partnerId: Long, reportId: Long, contributionId: Long): Boolean

    fun getAllContributionsForReportIds(reportIds: Set<Long>): List<ProjectPartnerReportEntityContribution>

    fun deleteByIds(ids: Set<Long>)

    fun updateExisting(toBeUpdated: Collection<UpdateProjectPartnerReportContributionExisting>)

    fun addNew(reportId: Long, toBeCreated: List<CreateProjectPartnerReportContribution>)

}
