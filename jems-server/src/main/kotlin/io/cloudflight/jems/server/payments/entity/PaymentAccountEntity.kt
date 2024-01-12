package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull


@Entity(name = "payment_account")
class PaymentAccountEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @field:NotNull
    var programmeFund: ProgrammeFundEntity,

    @ManyToOne
    @field:NotNull
    var accountingYear: AccountingYearEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: PaymentAccountStatus,

    @field:NotNull
    var nationalReference: String,

    @field:NotNull
    var technicalAssistance: BigDecimal,

    var submissionToSfcDate: LocalDate?,

    @field:NotNull
    var sfcNumber: String,

    @field:NotNull
    var comment: String,

)

