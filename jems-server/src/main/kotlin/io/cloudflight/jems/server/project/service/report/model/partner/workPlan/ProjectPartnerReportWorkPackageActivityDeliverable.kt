package io.cloudflight.jems.server.project.service.report.model.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

data class ProjectPartnerReportWorkPackageActivityDeliverable(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,

    val contribution: Boolean?,
    val evidence: Boolean?,
    val attachment: JemsFileMetadata?,
    val deactivated: Boolean
)
