package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectPartnerCoFinancingFundId(

    @Column
    @field:NotNull
    val partnerId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectPartnerCoFinancingFundTypeDTO

) : Serializable
