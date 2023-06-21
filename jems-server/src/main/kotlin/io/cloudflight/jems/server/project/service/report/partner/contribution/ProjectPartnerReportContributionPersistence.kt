package io.cloudflight.jems.server.project.service.report.partner.contribution

import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution

interface ProjectPartnerReportContributionPersistence {

    fun getPartnerReportContribution(partnerId: Long, reportId: Long): List<ProjectPartnerReportEntityContribution>

    fun existsByContributionId(partnerId: Long, reportId: Long, contributionId: Long): Boolean

    fun getAllContributionsForReportIds(reportIds: Set<Long>): List<ProjectPartnerReportEntityContribution>

    fun deleteByIds(ids: Set<Long>)

    fun updateExisting(toBeUpdated: Collection<UpdateProjectPartnerReportContributionExisting>)

    fun addNew(reportId: Long, toBeCreated: List<CreateProjectPartnerReportContribution>)

}
