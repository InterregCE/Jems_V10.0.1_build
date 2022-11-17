package io.cloudflight.jems.server.project.entity.contracting.partner

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_partner_beneficial_owner")
class ProjectContractingPartnerBeneficialOwnerEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    @field:NotNull
    val projectPartner: ProjectPartnerEntity,

    @field:NotNull
    var firstName: String,

    @field:NotNull
    var lastName: String,

    var birth: LocalDate?,

    @field:NotNull
    var vatNumber: String,
)
