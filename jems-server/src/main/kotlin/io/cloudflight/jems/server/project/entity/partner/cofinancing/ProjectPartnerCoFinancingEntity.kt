package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_co_financing")
data class ProjectPartnerCoFinancingEntity(

    @EmbeddedId
    val coFinancingFundId: ProjectPartnerCoFinancingFundId,

    @field:NotNull
    val percentage: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    val programmeFund: ProgrammeFundEntity?,

    )
