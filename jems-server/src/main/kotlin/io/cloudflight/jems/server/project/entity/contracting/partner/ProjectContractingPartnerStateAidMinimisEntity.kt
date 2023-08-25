package io.cloudflight.jems.server.project.entity.contracting.partner

import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.BaseForGranting
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "project_contracting_partner_state_aid_minimis")
class ProjectContractingPartnerStateAidMinimisEntity(

    @Id
    val partnerId: Long,

    val selfDeclarationSubmissionDate: ZonedDateTime?,

    @Enumerated(EnumType.STRING)
    val baseForGranting: BaseForGranting?,

    val aidGrantedByCountry: String?,

    val amountGrantingAid: BigDecimal?,

    @OneToMany(mappedBy = "id.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberStatesGranting: Set<ProjectContractingPartnerStateAidGrantedByMemberStateEntity> = setOf(),

    val comment: String?,
)
