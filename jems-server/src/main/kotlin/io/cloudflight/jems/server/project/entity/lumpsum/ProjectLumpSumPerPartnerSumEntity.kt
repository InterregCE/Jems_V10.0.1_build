package io.cloudflight.jems.server.project.entity.lumpsum

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import java.math.BigDecimal

data class ProjectLumpSumPerPartnerSumEntity(
    val partner: ProjectPartnerEntity,
    val sum: BigDecimal,
)
