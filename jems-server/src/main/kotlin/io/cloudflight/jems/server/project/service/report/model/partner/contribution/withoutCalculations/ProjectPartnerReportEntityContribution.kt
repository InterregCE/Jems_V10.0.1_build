package io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal
import java.util.UUID

data class ProjectPartnerReportEntityContribution(
    val id: Long,
    val reportId: Long,
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,
    val idFromApplicationForm: Long?,
    val historyIdentifier: UUID,
    val createdInThisReport: Boolean,
    val amount: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
    val attachment: JemsFileMetadata?,
)
