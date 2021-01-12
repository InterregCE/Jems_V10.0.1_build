package io.cloudflight.jems.server.project.entity.lumpsum

import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_lump_sum")
data class ProjectPartnerLumpSumEntity(

    @EmbeddedId
    val id: ProjectPartnerLumpSumId,

    @field:NotNull
    val amount: BigDecimal,

)
