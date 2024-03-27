package io.cloudflight.jems.server.project.entity.contracting

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_partner_payment_date")
class ProjectContractingPartnerPaymentDateEntity(

    @Id
    val partnerId: Long,

    // link to versioned table, use only for fetching projectId
    @ManyToOne
    @JoinColumn(name = "partner_id")
    @MapsId
    @field:NotNull
    val partner: ProjectPartnerEntity,

    @field:NotNull
    var lastPaymentDate: LocalDate,

)
