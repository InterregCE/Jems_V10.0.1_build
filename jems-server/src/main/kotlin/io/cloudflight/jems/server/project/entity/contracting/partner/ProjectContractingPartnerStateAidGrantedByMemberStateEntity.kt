package io.cloudflight.jems.server.project.entity.contracting.partner

import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_partner_state_aid_granted_by_member_state")
class ProjectContractingPartnerStateAidGrantedByMemberStateEntity (

    @EmbeddedId
    val id: ProjectContractingStateAidGrantedByMemberStateId,

    @field:NotNull
    val country: String,

    val amount: BigDecimal?,

    val selected: Boolean
)
