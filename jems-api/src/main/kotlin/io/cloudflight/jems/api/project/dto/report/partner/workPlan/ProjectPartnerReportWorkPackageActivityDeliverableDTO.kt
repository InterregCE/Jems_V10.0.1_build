package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportWorkPackageActivityDeliverableDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,

    val contribution: Boolean?,
    val evidence: Boolean?,
    val attachment: JemsFileMetadataDTO?,
    val deactivated: Boolean
)
