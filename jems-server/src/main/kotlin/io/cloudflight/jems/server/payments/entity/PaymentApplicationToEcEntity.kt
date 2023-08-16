package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import javax.persistence.Id
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Entity(name = "payment_applications_to_ec")
class PaymentApplicationToEcEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    @field:NotNull
    var programmeFund: ProgrammeFundEntity,

    @ManyToOne
    @JoinColumn(name = "accounting_year_id")
    @field:NotNull
    var accountingYear: AccountingYearEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: PaymentEcStatus
)
