package io.cloudflight.jems.server.project.entity.contracting

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_partner_payment_date")
class ContractingPartnerPaymentDateEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @field: NotNull
    val projectId: Long,

    @field: NotNull
    val partnerId: Long,

    val lastPaymentDate: LocalDate? = null,
)
