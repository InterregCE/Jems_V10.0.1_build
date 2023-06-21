package io.cloudflight.jems.server.project.entity.contracting.partner

import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.LocationInAssistedArea
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity(name = "project_contracting_partner_state_aid_gber")
class ProjectContractingPartnerStateAidGberEntity (

    @Id
    val partnerId: Long,

    val aidIntensity: BigDecimal?,

    @Enumerated(EnumType.STRING)
    val locationInAssistedArea: LocationInAssistedArea?,

    val comment: String?
)
